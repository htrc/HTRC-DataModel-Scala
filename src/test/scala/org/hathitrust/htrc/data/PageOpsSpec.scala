package org.hathitrust.htrc.data

import org.hathitrust.htrc.data.ops.PageOps
import org.hathitrust.htrc.data.ops.TextOptions._
import org.hathitrust.htrc.textprocessing.runningheaders.{Lines, Page}
import org.scalatest.{FunSuite, Matchers, ParallelTestExecution}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class PageOpsSpec extends FunSuite
  with ScalaCheckPropertyChecks with Matchers with ParallelTestExecution {

  val page: Page with PageOps = new Page with PageOps {
    override def textLines: Lines = IndexedSeq(
      "\tOnce upon a time  \r\n",
      "there was an  elephant named\r\n",
      "\"Dumbo\" who lived to-\r\n",
      "gether  with other ani-\r\n",
      "mals in Africa.\r\n",
      "\r\n",
      "   He was happy.  "
    )
  }

  test("TrimLines should trim whitespaces from the beginning and end of the line while preserving EOL") {
    page.text(TrimLines) shouldBe
      "Once upon a time\r\n" +
        "there was an  elephant named\r\n" +
        "\"Dumbo\" who lived to-\r\n" +
        "gether  with other ani-\r\n" +
        "mals in Africa.\r\n" +
        "\r\n" +
        "He was happy."
  }

  test("RemoveEmptyLines should remove empty lines while preserving EOL") {
    page.text(RemoveEmptyLines) shouldBe
      "\tOnce upon a time  \r\n" +
        "there was an  elephant named\r\n" +
        "\"Dumbo\" who lived to-\r\n" +
        "gether  with other ani-\r\n" +
        "mals in Africa.\r\n" +
        "   He was happy.  "
  }

  test("DehyphenateAtEol should combine hyphenated words across lines") {
    page.text(DehyphenateAtEol) shouldBe
      "\tOnce upon a time  \r\n" +
        "there was an  elephant named\r\n" +
        "\"Dumbo\" who lived together\r\n" +
        "with other animals\r\n" +
        "in Africa.\r\n" +
        "\r\n" +
        "   He was happy.  "
  }

  test("ParaLines should create paragraph groups") {
    page.text(ParaLines) shouldBe
      "\tOnce upon a time  there was an  elephant named \"Dumbo\" who lived to- gether  with other ani- mals in Africa.\r\n" +
        "   He was happy.  "
  }

  test("Applying multiple TextOptions produces the correct result") {
    page.text(TrimLines, DehyphenateAtEol, ParaLines) shouldBe
      "Once upon a time there was an  elephant named \"Dumbo\" who lived together with other animals in Africa.\r\n" +
        "He was happy."
  }
}
