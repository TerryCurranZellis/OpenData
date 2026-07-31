-- Inserts a table of contents at a manifest-controlled marker.
-- The marker must be an empty Div with class "document-toc".

local toc_headers = {}
local toc_depth = 3
local generate_toc = true

local docx_toc = [[
<w:sdt>
  <w:sdtPr>
    <w:docPartObj>
      <w:docPartGallery w:val="Table of Contents"/>
      <w:docPartUnique/>
    </w:docPartObj>
  </w:sdtPr>
  <w:sdtContent>
    <w:p>
      <w:pPr><w:pStyle w:val="TOCHeading"/></w:pPr>
      <w:r><w:t xml:space="preserve">Table of Contents</w:t></w:r>
    </w:p>
    <w:p>
      <w:r>
        <w:fldChar w:fldCharType="begin" w:dirty="true"/>
        <w:instrText xml:space="preserve">TOC \o &quot;1-3&quot; \h \z \u</w:instrText>
        <w:fldChar w:fldCharType="separate"/>
        <w:fldChar w:fldCharType="end"/>
      </w:r>
    </w:p>
  </w:sdtContent>
</w:sdt>
]]

local docx_page_break = [[
<w:p><w:r><w:br w:type="page"/></w:r></w:p>
]]

local function metadata_boolean(value, default_value)
  if value == nil then
    return default_value
  end
  local text = pandoc.utils.stringify(value):lower()
  if text == 'false' or text == 'no' or text == '0' then
    return false
  end
  if text == 'true' or text == 'yes' or text == '1' then
    return true
  end
  return default_value
end

local function metadata_number(value, default_value)
  if value == nil then
    return default_value
  end
  return tonumber(pandoc.utils.stringify(value)) or default_value
end

local function is_unlisted(header)
  return header.classes:includes('unlisted')
end

local function html_toc_blocks()
  local items = {}
  for _, header in ipairs(toc_headers) do
    local prefix = string.rep('— ', math.max(0, header.level - 1))
    local content = {}
    if prefix ~= '' then
      table.insert(content, pandoc.Str(prefix))
    end
    table.insert(content, pandoc.Link(header.content, '#' .. header.identifier))
    table.insert(items, { pandoc.Plain(content) })
  end

  return {
    pandoc.Header(1, 'Table of Contents', pandoc.Attr('', { 'unnumbered', 'unlisted' })),
    pandoc.Div(
      { pandoc.BulletList(items) },
      pandoc.Attr('table-of-contents', { 'document-table-of-contents' })
    )
  }
end

local function suppress_automatic_title_block(document)
  if FORMAT:match('html') then
    if document.meta.title ~= nil and document.meta.pagetitle == nil then
      document.meta.pagetitle = document.meta.title
    end
    document.meta.title = nil
    document.meta.author = nil
    document.meta.date = nil
  elseif FORMAT:match('latex') then
    if document.meta.title ~= nil and document.meta['title-meta'] == nil then
      document.meta['title-meta'] = document.meta.title
    end
    if document.meta.author ~= nil and document.meta['author-meta'] == nil then
      document.meta['author-meta'] = document.meta.author
    end
    document.meta.title = nil
    document.meta.author = nil
    document.meta.date = nil
  end
end

function Pandoc(document)
  suppress_automatic_title_block(document)
  generate_toc = metadata_boolean(document.meta['opendata-generate-toc'], true)
  toc_depth = metadata_number(document.meta['opendata-toc-depth'], 3)
  toc_headers = {}

  document.blocks = document.blocks:walk({
    Header = function(header)
      if generate_toc and header.level <= toc_depth and not is_unlisted(header) then
        table.insert(toc_headers, header)
      end
      return nil
    end
  })

  document.blocks = document.blocks:walk({
    Div = function(div)
      if not div.classes:includes('document-toc') then
        return nil
      end
      if not generate_toc then
        return {}
      end

      if FORMAT:match('docx') then
        local xml = docx_toc:gsub('1%-3', '1-' .. tostring(toc_depth))
        return {
          pandoc.RawBlock('openxml', xml),
          pandoc.RawBlock('openxml', docx_page_break)
        }
      end

      if FORMAT:match('latex') then
        return {
          pandoc.RawBlock('latex', '\\tableofcontents'),
          pandoc.RawBlock('latex', '\\clearpage')
        }
      end

      return html_toc_blocks()
    end
  })

  return document
end
