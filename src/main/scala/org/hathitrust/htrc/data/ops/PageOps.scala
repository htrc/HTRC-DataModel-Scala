package org.hathitrust.htrc.data.ops

import org.hathitrust.htrc.data.ops.TextOptions._
import org.hathitrust.htrc.textprocessing.runningheaders.{Lines, Page}
import org.hathitrust.htrc.tools.scala.implicits.CollectionsImplicits._

import scala.util.matching.Regex

object PageOps {
  protected val EndParagraphPunct: Set[Char] = Set('.', '?', '!')
  protected val splitEolRegex: Regex = """^(.*)(\R?)$""".r

  def splitEol(line: String): (String, String) = line match {
    case splitEolRegex(s, eol) => s -> eol
  }
}

trait PageOps {
  self: Page =>

  import PageOps._

  def text(textOptions: TextOptions*): String = applyTextOptions(textLines, textOptions).mkString

  def textLines(textOptions: TextOptions*): Lines = applyTextOptions(textLines, textOptions).toIndexedSeq

  protected def applyTextOptions(textLines: Lines, textOptions: Seq[TextOptions]): Iterator[String] = {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var lines = textLines.iterator

    textOptions match {
      case Nil =>
      case _ =>
        if (textOptions contains TrimLines)
          lines = lines.map(splitEol).map { case (s, eol) => s.trim concat eol }

        if (textOptions contains RemoveEmptyLines)
          lines = lines.filterNot(splitEol(_)._1.isEmpty)

        if (textOptions contains DehyphenateAtEol)
          lines = lines.dehyphenate()

        if (textOptions contains ParaLines)
          lines = lines.map(splitEol).filter(_._1.nonEmpty)
            .groupConsecutiveWhen((l1, _) => !EndParagraphPunct.contains(l1._1.last))
            .map(
              _.reduceOption { (l1, l2) =>
                val space = if (l1._1.endsWith(" ") || l2._1.startsWith(" ")) "" else " "
                val para = l1._1 concat space concat l2._1
                para -> l2._2
              } match {
                case Some((s, eol)) => s concat eol
                case None => ""
              }
            )
    }

    lines
  }
}
