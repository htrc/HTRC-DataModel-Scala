package org.hathitrust.htrc.data.ops

import org.hathitrust.htrc.data.ops.TextOptions.TextOptions
import org.hathitrust.htrc.textprocessing.runningheaders.PageStructure

trait StructuredPageOps { self: PageStructure with PageOps =>

  def header(textOptions: TextOptions*): String = applyTextOptions(headerLines, textOptions)

  def body(textOptions: TextOptions*): String = applyTextOptions(bodyLines, textOptions)

  def footer(textOptions: TextOptions*): String = applyTextOptions(footerLines, textOptions)

}
