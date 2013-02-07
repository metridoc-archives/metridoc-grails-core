package metridoc.admin

import org.apache.commons.lang.text.StrBuilder
import java.util.regex.Pattern

/**
 * Created with IntelliJ IDEA.
 * User: dongheng
 * Date: 8/24/12
 * Time: 10:39 AM
 * To change this template use File | Settings | File Templates.
 */
class LogService {

    def grailsApplication
    public static final ONE_HOUR = 1000 * 60 * 60
    public static final SIX_HOURS = ONE_HOUR * 6
    public static final TWELVE_HOURS = SIX_HOURS * 2
    public static final ONE_DAY = TWELVE_HOURS * 2
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

    enum LineType {
        INFO, ERROR, WARN
    }

    /**
     * Convert logs in given log file into a html page content
     * @param response
     * @param file  logFile which needs to be converted into html
     */
    public void renderLog(response, file) {

        def previous = LineType.INFO
        def lineNum = 0
        file.eachLine {String line ->
            def escapedLine = escape(line)
            def div = addDiv(escapedLine, previous)
            def divLine = div.line
            previous = div.previous
            response << divLine
            lineNum++
        }
    }
    /**
     * Translate all special character in logging line into html representation.
     * @param s a logging line
     * @return a logging line with all special char converted to html representation
     */
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
    /**
     * Get the type of a logging line.
     * If the line given as argument doesn't contain LineType, return the type of previous line
     * @param line a logging line
     * @param previous lineType of previous logging line
     * @return lineType of the line given as argument
     */
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
    /**
     * Categorize a logging line's time.
     * If a line doesn't contain time information, categorize it as the time of last line.
     * @param line escaped logging line content
     * @param previousDate Time range of the previous line
     * @param now Current time
     * @return Time range of the line in argument
     */
    private static getDateClass(line, previousDate, now) {
        def m = line =~ /(\d{4}-\d{2}-\d{2}\s\d{2}:\d{2}:\d{2})/
        def result = "all"
        if(m.lookingAt()) {
            def date = Date.parse(DATE_FORMAT, m.group(1)).getTime()

            def dateTest = {
                def nowTime = now.time
                def difference = nowTime - it
                date > difference
            }

            if(dateTest(ONE_HOUR)) {
                result += " hour"
            }

            if(dateTest(SIX_HOURS)) {
                result += " sixHours"
            }

            if(dateTest(TWELVE_HOURS)) {
                result += " twelveHours"
            }

            if(dateTest(ONE_DAY)) {
                result += " day"
            }

            return result
        } else {
            return previousDate
        }
    }

    /**
     * Get a logging line's LogName.
     * If given line doesn't contain a logName, use previous line's logName.
     * @param a logging line
     * @param previousLogName LogName of the previous line
     * @return logName of the line given in argument
     */
    private static getLogNameClass(line, previousLogName) {

        def m = line =~ /(INFO|WARN)\s\&nbsp\;([^\s]*)\s\&nbsp\;-/
        def n = line =~ /(ERROR)\s([^\s]*)\s\&nbsp\;-/
        if(m.find()) {
            return m.group(2)
        }else if(n.find()){
            return n.group(2)
        } else {
            return previousLogName
        }
    }
    /**
     * Each line in log file corresponds to a <div> in html
     * @param line  line content
     * @param previous   line type of previous logging line
     * @param previousDateClass   DateType of previous logging line
     * @param previousLogNameClass  LogName of previous logging line
     * @param lineNum  index of the line given as argument
     * @return
     */
    def addDiv(String line, previous) {


        def addLine = {clazz, color->
            def result= "<div "+
                    "class=\"content logLine ${clazz}\" "+
                    "style=\"color:${color}\">${line}</div>"
            return result
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

    }
}
