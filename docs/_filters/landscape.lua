-- Converts fenced Divs with class "landscape" into landscape sections/pages.
-- DOCX receives A4 section breaks; LaTeX/PDF receives pdflscape boundaries.

local portrait_section = [[
<w:p>
  <w:pPr>
    <w:sectPr>
      <w:type w:val="nextPage"/>
      <w:pgSz w:w="11906" w:h="16838"/>
      <w:pgMar w:top="1134" w:right="1134" w:bottom="1134" w:left="1134"
               w:header="720" w:footer="720" w:gutter="0"/>
    </w:sectPr>
  </w:pPr>
</w:p>
]]

local landscape_section = [[
<w:p>
  <w:pPr>
    <w:sectPr>
      <w:type w:val="nextPage"/>
      <w:pgSz w:w="16838" w:h="11906" w:orient="landscape"/>
      <w:pgMar w:top="1134" w:right="1134" w:bottom="1134" w:left="1134"
               w:header="720" w:footer="720" w:gutter="0"/>
    </w:sectPr>
  </w:pPr>
</w:p>
]]

local function append_all(target, source)
  for _, value in ipairs(source) do
    table.insert(target, value)
  end
end

local function cell_inlines(cell)
  local inlines = {}
  for _, block in ipairs(cell.contents) do
    if block.t == 'Plain' or block.t == 'Para' then
      append_all(inlines, block.content)
    else
      table.insert(inlines, pandoc.Str(pandoc.utils.stringify(block)))
    end
  end
  return inlines
end

local function table_as_labelled_entries(tbl)
  local headers = {}
  if #tbl.head.rows > 0 then
    for index, cell in ipairs(tbl.head.rows[1].cells) do
      headers[index] = pandoc.utils.stringify(cell.contents)
    end
  end

  local items = {}
  for _, body in ipairs(tbl.bodies) do
    for _, row in ipairs(body.body) do
      local item = {}
      for index, cell in ipairs(row.cells) do
        local value = cell_inlines(cell)
        if #value > 0 and pandoc.utils.stringify(value) ~= '' then
          if index == 1 then
            table.insert(item, pandoc.Para({ pandoc.Strong(value) }))
          else
            local label = headers[index] or ('Column ' .. index)
            local content = {
              pandoc.Strong({ pandoc.Str(label .. ':') }),
              pandoc.Space()
            }
            append_all(content, value)
            table.insert(item, pandoc.Para(content))
          end
        end
      end
      if #item > 0 then
        table.insert(items, item)
      end
    end
  end
  return pandoc.BulletList(items)
end

function Image(image)
  if FORMAT:match('latex') and image.src:lower():match('%.svg$') then
    image.src = image.src:gsub('%.svg$', '.pdf')
  end
  return image
end

function Table(tbl)
  if FORMAT:match('docx') then
    return table_as_labelled_entries(tbl)
  end
  return nil
end

function RawBlock(block)
  if FORMAT:match('docx') and block.format == 'tex' and
      block.text:match('^\\newpage%s*$') then
    -- Chapter breaks are applied to Heading 1 paragraphs in the DOCX
    -- post-processing step. Removing explicit break paragraphs avoids blank
    -- pages when a chapter follows a landscape section boundary.
    return {}
  end
  return nil
end

function Div(div)
  local landscape_figure = div.classes:includes('landscape')
  local landscape_table = div.classes:includes('landscape-table')
  local docx_linear_table = div.classes:includes('docx-linear-table')
  if not landscape_figure and not landscape_table and not docx_linear_table then
    return nil
  end

  if FORMAT:match('docx') and landscape_figure then
    local blocks = { pandoc.RawBlock('openxml', portrait_section) }
    append_all(blocks, div.content)
    table.insert(blocks, pandoc.RawBlock('openxml', landscape_section))
    return blocks
  end

  if FORMAT:match('docx') and
      (landscape_table or docx_linear_table) then
    local blocks = {}
    for _, block in ipairs(div.content) do
      if block.t == 'Table' then
        table.insert(blocks, table_as_labelled_entries(block))
      else
        table.insert(blocks, block)
      end
    end
    return blocks
  end

  if FORMAT:match('latex') and
      (landscape_figure or landscape_table) then
    local blocks = { pandoc.RawBlock('latex', '\\begin{landscape}') }
    append_all(blocks, div.content)
    table.insert(blocks, pandoc.RawBlock('latex', '\\end{landscape}'))
    return blocks
  end

  return div
end
