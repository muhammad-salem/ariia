package org.ariia.monitors;

import org.ariia.util.Utils;
import org.terminal.Ansi;
import org.terminal.ansi.CursorControl;
import org.terminal.beans.ColumnStyle;
import org.terminal.strings.StyleBuilder;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <pre>
 * ┌─┬──────────────────────────────────┬─────────────────┬──────────────┐
 * │#│         Name                     │ Complete/Remain │  Down/Speed  │
 * ├─┼──────────────────────────────────┼─────────────────┼──────────────┤
 *
 * │1│ debian-live-9.5.0-amd64-kde.iso  │     2.083 GB    │   104.40 MB  │
 *
 * │ │     2.412 GB   -   (86.354 %)    │   337.093 MB    │  14.81 KB/s  │
 *
 * ├─┼──────────────────────────────────┼─────────────────┼──────────────┤
 *
 * │2│ debian-live-9.5.0-amd64-kde.iso  │     2.083 GB    │   104.40 MB  │
 *
 * │ │     2.412 GB   -   (86.354 %)    │   337.093 MB    │  14.81 KB/s  │
 *
 * ├─┼──────────────────────────────────┼─────────────────┼──────────────┤
 *
 * │#│           Session (2)            │     2.083 GB    │   104.40 MB  │
 *
 * │ │     2.412 GB       (86.354 %)    │   337.093 MB    │  14.81 KB/s  │
 *
 * └─┴──────────────────────────────────┴─────────────────┴──────────────┘
 * </pre>
 *
 * @author salem
 */
public class MiniSpeedTableReport implements SpeedTableReport, CursorControl {

    protected Set<RangeReport> monitors;
    protected SessionReport session;
    protected List<ColumnStyle> dataStyle;
    String header, midBorder, bodyTemplate, footer;
    StyleBuilder borderStyle;
    MessageFormat format;


    public MiniSpeedTableReport(SessionReport session) {
        this.session = session;
        this.monitors = new HashSet<>();
        initColumnStyle();
        initTemplates();
        this.format = new MessageFormat(bodyTemplate);
    }

    void initTemplates() {
        borderStyle = new StyleBuilder();
        borderStyle.bold().color(22, 200, 44);

        this.header = borderStyle.build(
                "┌─┬─────────────────────────────────────────┬─────────────────┬──────────────┐\n" +
                        "│#│                   Name                  │ Complete/Remain │  Speed/Down  │\n");
        this.midBorder = borderStyle.build(
                "├─┼─────────────────────────────────────────┼─────────────────┼──────────────┤\n");
        this.footer = borderStyle.build(
                "└─┴─────────────────────────────────────────┴─────────────────┴──────────────┘");

        var builder = new StringBuilder();
        builder.append(borderStyle.build("│"));
        builder.append(dataStyle.get(0).build("{0}"));        // w = 1
        builder.append(borderStyle.build("│"));
        builder.append(dataStyle.get(1).build("{1}"));        // w = 41
        builder.append(borderStyle.build("│"));
        builder.append(dataStyle.get(2).build("{2}"));        // w = 18
        builder.append(borderStyle.build("│"));
        builder.append(dataStyle.get(3).build("{3}"));        // w = 14
        builder.append(borderStyle.build("│"));
        builder.append('\n');

        builder.append(borderStyle.build("│ │"));
        builder.append("  ");
        builder.append(dataStyle.get(4).build("        {4}"));        // w = 10
        builder.append("  -  ");
        builder.append(dataStyle.get(4).build("({5})"));        // w = 10
        builder.append("    ");
        builder.append(borderStyle.build("│"));
        builder.append(dataStyle.get(6).build("{6}"));        // w = 17
        builder.append(borderStyle.build("│"));
        builder.append(dataStyle.get(6).build("{7}"));        // w = 14
        builder.append(borderStyle.build("│"));
        builder.append('\n');


        // │{0}│{1}│{2}│{3}│
        // │ │{4} - ({5})│{6}│{7}│

        this.bodyTemplate = builder.toString();
    }

    protected void initColumnStyle() {
        dataStyle = new ArrayList<>();
        // index
        ColumnStyle style = new ColumnStyle();
        style.cyan();
        dataStyle.add(style);

        // name
        style = new ColumnStyle();
        style.blueLite().bold();
        dataStyle.add(style);

        //complete
        style = new ColumnStyle();
        style.magentaLite();
        dataStyle.add(style);

        // "Down", 
        style = new ColumnStyle();
        style.magentaLite();
        dataStyle.add(style);

        //length
        style = new ColumnStyle();
        style.red().bold();
        dataStyle.add(style);

        //  "%"
        style = new ColumnStyle();
        style.redLite();
        dataStyle.add(style);

        // "Remain",
        style = new ColumnStyle();
        style.yellow();
        dataStyle.add(style);

        //"speed",
        style = new ColumnStyle();
        style.blueLite();
        dataStyle.add(style);

    }

    @Override
    public boolean add(RangeReport monitor) {
        return monitors.add(monitor);
    }

    @Override
    public void remove(RangeReport monitor) {
        monitors.remove(monitor);
    }

    @Override
    public void clear() {
        monitors.clear();
    }

    @Override
    public String getTableReport() {
        updateInfo();
        var message = new StringBuilder();
        message.append(Ansi.EraseDown);
        message.append('\n');
        message.append('\n');
        message.append('\n');

        buildTable(message);

        message.append('\n');
        message.append(' ');
        message.append(session.getTimer());
        message.append(' ');
        message.append(session.progressLine(58));
        message.append(' ');
        message.append(session.getRemainingTimeString());
        message.append('\n');

        String all = message.toString();
        int count = 1;
        for (byte c : all.getBytes()) {
            if (c == '\n')
                count++;
        }
        message.append(cursorUp(count));
        callSpeedForNextCycle();
        return message.toString();
    }

    @Override
    public void updateOneCycle() {
        updateInfo();
        callSpeedForNextCycle();
    }


    private void callSpeedForNextCycle() {
        for (var monitor : monitors) {
            monitor.snapshotPoint();
        }
        session.snapshotPoint();
    }

    private void updateInfo() {
        for (var monitor : monitors) {
            monitor.snapshotSpeed();
        }
        session.snapshotSpeed();
    }


    private void buildTable(StringBuilder message) {
        message.append(header);
        int index = 0;
        for (var monitor : monitors) {
            Object[] obj = new Object[8];
            obj[0] = ++index + "";
            obj[1] = Utils.middleMaxLength(monitor.getName(), 41);
            obj[2] = Utils.middleMaxLength(monitor.getDownloadLength(), 17);
            obj[3] = Utils.middleMaxLength(monitor.getTcpDownloadSpeed() + "/s", 14);

            obj[4] = Utils.middleMaxLength(monitor.getFileLength(), 10);
            obj[5] = Utils.middleMaxLength(monitor.getPercent(), 10);
            obj[6] = Utils.middleMaxLength(monitor.getRemainingLength(), 17);
            obj[7] = Utils.middleMaxLength(monitor.getTcpDownload(), 14);

            message.append(midBorder);
            message.append(format.format(obj));
        }
        if (session.rangeSize() != 1) {
            Object[] obj = new Object[8];
            obj[0] = "#";
            obj[1] = Utils.middleMaxLength("Session (" + session.rangeSize() + ")", 41);
            obj[2] = Utils.middleMaxLength(session.getDownloadLengthMB(), 17);
            obj[3] = Utils.middleMaxLength(session.getTcpDownloadSpeed() + "/s", 14);

            obj[4] = Utils.middleMaxLength(session.getTotalLengthMB(), 10);
            obj[5] = Utils.middleMaxLength(session.getPercent(), 10);
            obj[6] = Utils.middleMaxLength(session.getRemainingLengthMB(), 17);
            obj[7] = Utils.middleMaxLength(session.getTcpDownload(), 14);

            message.append(midBorder);
            message.append(format.format(obj));
        }
        message.append(footer);
    }

    @Override
    public SessionReport getSessionMonitor() {
        return session;
    }

}
