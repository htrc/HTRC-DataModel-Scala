package org.hathitrust.htrc.data.ops

import org.hathitrust.htrc.data.ops.TextOptions.TextOptions
import org.hathitrust.htrc.textprocessing.runningheaders.{Lines, PageStructure}

trait StructuredPageOps { self: PageStructure with PageOps =>

  def header(textOptions: TextOptions*): String = applyTextOptions(headerLines, textOptions).mkString

  def headerLines(textOptions: TextOptions*): Lines = applyTextOptions(headerLines, textOptions).toIndexedSeq

  def body(textOptions: TextOptions*): String = applyTextOptions(bodyLines, textOptions).mkString

  def bodyLines(textOptions: TextOptions*): Lines = applyTextOptions(bodyLines, textOptions).toIndexedSeq

  def footer(textOptions: TextOptions*): String = applyTextOptions(footerLines, textOptions).mkString

  def footerLines(textOptions: TextOptions*): Lines = applyTextOptions(footerLines, textOptions).toIndexedSeq

}
