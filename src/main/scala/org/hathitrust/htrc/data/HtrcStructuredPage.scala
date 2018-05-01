package org.hathitrust.htrc.data

import org.hathitrust.htrc.data.TextOptions.TextOptions
import org.hathitrust.htrc.textprocessing.runningheaders.{Lines, PageStructure}

class HtrcStructuredPage(override val seq: String,
                         override val textLines: Lines,
                         val numHeaderLines: Int,
                         val numFooterLines: Int) extends HtrcPage(seq, textLines) with PageStructure {

  def header(textOptions: TextOptions*): String = applyTextOptions(headerLines, textOptions)

  def body(textOptions: TextOptions*): String = applyTextOptions(bodyLines, textOptions)

  def footer(textOptions: TextOptions*): String = applyTextOptions(footerLines, textOptions)
}
