package metridoc.admin

import org.apache.commons.lang.text.StrBuilder

/**
 * Created with IntelliJ IDEA.
 * User: dongheng
 * Date: 8/24/12
 * Time: 10:39 AM
 * To change this template use File | Settings | File Templates.
 */
class LogService {

    def grailsApplication
    enum LineType {
        INFO, ERROR, WARN
    }


    public void renderLog(response, file) {


        def previous = LineType.INFO
        file.eachLine {String line ->
            def escapedLine = escape(line)
            def div = addDiv(escapedLine, previous)
            def divLine = div.line
            previous = div.previous
            response << divLine
        }
    }

    public static String escape(String s) {
        StringBuilder builder = new StringBuilder();
        boolean previousWasASpace = false;
        for (char c : s.toCharArray()) {
            if (c == ' ') {
                if (previousWasASpace) {
                    builder.append("&nbsp;");
                    previousWasASpace = false;
                    continue;
                }
                previousWasASpace = true;
            } else {
                previousWasASpace = false;
            }
            switch (c) {
                case '<': builder.append("&lt;"); break;
                case '>': builder.append("&gt;"); break;
                case '&': builder.append("&amp;"); break;
                case '"': builder.append("&quot;"); break;
                case '\n': builder.append("<br/>"); break;
            // We need Tab support here, because we print StackTraces as HTML
                case '\t': builder.append("&nbsp; &nbsp; &nbsp;"); break;
                default:
                    if (c < 128) {
                        builder.append(c);
                    } else {
                        builder.append("&#").append((int) c).append(";");
                    }
            }
        }
        return builder.toString();
    }

    public LineType getLineType(line, previous) {
        if (line.contains(LineType.INFO.toString())) {
            return LineType.INFO
        }

        if (line.contains(LineType.ERROR.toString()) ||
                line.contains("Exception") ||
                line.contains("at ")) {
            return LineType.ERROR
        }

        if (line.contains(LineType.WARN.toString())) {
            return LineType.WARN
        }

        return previous
    }

    def addDiv(String line, previous) {

        def addLine = {clazz, color->
            "<div class=\"content ${clazz}\" style=\"color:${color}\">${line}</div>"
        }

        def result
        def type = getLineType(line, previous)
        switch (type) {
            case LineType.ERROR:
                result = addLine("error", "red")
                break
            case LineType.WARN:
                result = addLine("warn", "#CCCC66")
                break
            default:
                result = addLine("info", "green")
        }

        return [line: result, previous:type]

//        int previousIndex = 0;
//        int currentIndex = str.indexOf("<br>", previousIndex);
//        StringBuffer resultBuffer = new StringBuffer();
//        if( currentIndex != -1 )
//        {
//            resultBuffer.append( "<div class=\"title\">"+
//                    str.substring( previousIndex, currentIndex ) );
//        }
//
//        previousIndex=currentIndex;
//        currentIndex = str.indexOf("<br>", previousIndex + 1);
//
//        while( currentIndex != -1 )
//        {
//            if( previousIndex+4 >= currentIndex )
//            {
//                break;
//            }
//            String currentLine = str.substring( previousIndex + 4, currentIndex );
//            String currentLineInDiv;
//            if( currentLine.contains("INFO") )
//            {
//                currentLineInDiv = "</div>\n<div class=\"content info\">" + currentLine;
//            }
//            else if( currentLine.contains("WARN") )
//            {
//                currentLineInDiv = "</div>\n<div class=\"content warn\">" + currentLine;
//            }
//            else if( currentLine.contains("ERROR") )
//            {
//                currentLineInDiv = "</div>\n<div class=\"content error\">" + currentLine;
//            }
//            else
//            {
//                currentLineInDiv = "<br>" + currentLine;
//            }
//
//            resultBuffer.append( currentLineInDiv );
//
//            //Go to next line
//            previousIndex = currentIndex;
//            currentIndex = str.indexOf("<br>", previousIndex + 1);
//        }
//        resultBuffer.append("</div>");
//
//        return resultBuffer.toString();
    }
}
