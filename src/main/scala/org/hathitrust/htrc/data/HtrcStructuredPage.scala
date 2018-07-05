package org.hathitrust.htrc.data

import org.hathitrust.htrc.data.ops.StructuredPageOps
import org.hathitrust.htrc.textprocessing.runningheaders.{Lines, PageStructure}

class HtrcStructuredPage(override val seq: String,
                         override val textLines: Lines,
                         val numHeaderLines: Int,
                         val numFooterLines: Int) extends HtrcPage(seq, textLines) with PageStructure with StructuredPageOps