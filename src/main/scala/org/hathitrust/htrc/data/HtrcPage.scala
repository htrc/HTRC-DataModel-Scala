package org.hathitrust.htrc.data

import java.io.InputStream

import org.hathitrust.htrc.data.TextOptions._
import org.hathitrust.htrc.textprocessing.runningheaders.{Lines, Page}
import org.hathitrust.htrc.tools.scala.implicits.CollectionsImplicits._

import scala.io.{Codec, Source}

object HtrcPage {
  protected val EndParagraphPunct: Set[Char] = Set('.', '?', '!')
}

class HtrcPage(val seq: String, val textLines: Lines) extends Page {

  import HtrcPage._

  def this(seq: String, lines: Iterator[String]) = this(seq, lines.toIndexedSeq)

  def this(seq: String, source: Source) = this(seq, source.getLines())

  def this(seq: String, text: String) = this(seq, Source.fromString(text))

  def this(seq: String, stream: InputStream)
          (implicit codec: Codec) = this(seq, Source.fromInputStream(stream))

  def text(textOptions: TextOptions*): String = applyTextOptions(textLines, textOptions)

  protected def applyTextOptions(textLines: Lines, textOptions: Seq[TextOptions]): String =
    textOptions match {
      case Nil => text
      case _ =>
        var lines = textLines.iterator

        if (textOptions contains TrimLines)
          lines = lines.map(_.trim)

        if (textOptions contains RemoveEmptyLines)
          lines = lines.filter(_.nonEmpty)

        if (textOptions contains FixHyphenation)
          lines = lines.dehyphenate()

        if (textOptions contains ParaLines)
          lines = lines.filter(_.nonEmpty)
            .groupConsecutiveWhen((l1, _) => !EndParagraphPunct.contains(l1.last))
            .map(_.reduce(_ + " " + _))

        lines.mkString("", System.lineSeparator(), System.lineSeparator())
    }

  override def toString: String = f"HtrcPage($seq, ${textLines.length}%,d lines)"
}
