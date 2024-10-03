
package com.pasc.lib.log.formatter.message.xml;

import com.pasc.lib.log.formatter.FormatException;
import com.pasc.lib.log.internal.SystemCompat;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Simply format the XML with a indent of {@value XML_INDENT}.
 * <br>TODO: Make indent size and enable/disable state configurable.
 */
public class DefaultXmlFormatter implements XmlFormatter {

  private static final int XML_INDENT = 4;

  @Override
  public String format(String xml) {
    String formattedString;
    if (xml == null || xml.trim().length() == 0) {
      throw new FormatException("XML empty.");
    }
    try {
      Source xmlInput = new StreamSource(new StringReader(xml));
      StreamResult xmlOutput = new StreamResult(new StringWriter());
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
          String.valueOf(XML_INDENT));
      transformer.transform(xmlInput, xmlOutput);
      formattedString = xmlOutput.getWriter().toString().replaceFirst(">", ">"
          + SystemCompat.lineSeparator);
    } catch (Exception e) {
      throw new FormatException("Parse XML error. XML string:" + xml, e);
    }
    return formattedString;
  }
}
