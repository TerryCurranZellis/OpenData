# Requirements
create a powershell script to make changes to all Java source files within all folders and subfolders

Starting folder is 
`C:\Powershell\Modules\OpenData\src`

## Make the following changes

### Change 1
- If there is a license header
```text
/*
 *  Filename: CommandLineArguments.java
 * 
 *  (C) Copyright Terry Curran 2026. All rights reserved
 * 
 *  This software is provided 'as-is', without any express or implied
 *  warranty.  In no event will the author be held liable for any damages
 *  arising from the use of this software.
 * 
 *  Permission is granted to anyone to use this software for any purpose,
 *  including commercial applications, and to alter it and redistribute it
 *  freely, subject to the following restrictions:
 * 
 *  1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software
 *     in a product, an acknowledgement in the product documentation would be
 *     appreciated but is not required.
 *  2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 *  3. This notice may not be removed or altered from any source distribution.
 * 
 *  The author may be contacted by email to the following address:
 * 
 *  terry.curran@towermarsh.co.uk
 */
```
- Replace with
```text
 /*
  * Filename: CommandLineArguments.java
  *
  * (c) Copyright 2026 Terry Curran
  *
  * SPDX-License-Identifier: Apache-2.0
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     https://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  * 
  *  The author may be contacted by email to the following address:
  * 
  *  terry.curran@towermarsh.co.uk
  */
```
- if there is no licence header insert the new license header.
- in the line `* Filename: CommandLineArguments.java` the filename is the name of the Java source file

### Change 2

Above the class definitin which starts with the word `public` there may already be a comment block delimited by `/**` and `*/`

- If this block does not contain
```text
@author terry
```
After any text within the block and before the end delimiter */
Insert a line with `*`
Insert a line with `* @author Terry Curran`
insert a line with `* @version 21 Jul 2026`
 
- If this block contains
```text
@author terry
```
Update to `@author Terry Curran`
Remove the line `* @author (C) Copyright Terry Curran 2026. All Rights Reserved.`

Save the amended file
