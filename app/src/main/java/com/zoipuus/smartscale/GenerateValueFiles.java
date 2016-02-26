package com.zoipuus.smartscale;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Created by Administrator on 2015/12/14.
 * 代码参考http://blog.csdn.net/lmj623565791/article/details/45460089；
 */
public class GenerateValueFiles {

    private final String dirStr = "./app/src/main/res";

    private final String SUPPORT_DIMENSION = "320,480;480,800;480,854;540,960;600,1024;720,1184;720,1196;720,1280;768,1024;800,1280;1080,1812;1080,1920;1200,1920;1440,2560;";

    private String supportStr = SUPPORT_DIMENSION;

    public GenerateValueFiles(String supportStr) {

        this.supportStr += validateInput(supportStr);

        System.out.println(supportStr);

        File dir = new File(dirStr);
        if (!dir.exists()) {
            dir.mkdir();

        }
        System.out.println(dir.getAbsoluteFile());

    }

    /**
     * @param supportStr w,h_...w,h;
     * @return String
     */
    private String validateInput(String supportStr) {
        StringBuilder sb = new StringBuilder();
        String[] values = supportStr.split("_");
        int w;
        int h;
        String[] wh;
        for (String val : values) {
            try {
                if (val == null || val.trim().length() == 0)
                    continue;

                wh = val.split(",");
                w = Integer.parseInt(wh[0]);
                h = Integer.parseInt(wh[1]);
            } catch (Exception e) {
                System.out.println("skip invalidate params : w,h = " + val);
                continue;
            }
            sb.append(w).append(",").append(h).append(";");
        }

        return sb.toString();
    }

    public void generate() {
        String[] vals = supportStr.split(";");
        for (String val : vals) {
            String[] wh = val.split(",");
            generateXmlFile(Integer.parseInt(wh[0]), Integer.parseInt(wh[1]));
        }

    }

    private void generateXmlFile(int w, int h) {
        StringBuilder sbForWidth = new StringBuilder();
        sbForWidth.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        sbForWidth.append("<resources>");
        final int portion = 100;
        final float cell_w = w * 1.00f / portion;

        final String WTemplate = "<dimen name=\"w{0}\">{1}</dimen>\n";
        for (int i = 1; i < portion; i++) {
            sbForWidth.append(WTemplate.replace("{0}", i + "").replace("{1}",
                    change(cell_w * i) + ""));
        }
        sbForWidth.append(WTemplate.replace("{0}", portion + "").replace("{1}",
                w + ""));
        sbForWidth.append("</resources>");

        StringBuilder sbForHeight = new StringBuilder();
        sbForHeight.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        sbForHeight.append("<resources>");
        final float cell_h = h * 1.00f / portion;
        final String HTemplate = "<dimen name=\"h{0}\">{1}</dimen>\n";
        for (int i = 1; i < portion; i++) {
            sbForHeight.append(HTemplate.replace("{0}", i + "").replace("{1}",
                    change(cell_h * i) + ""));
        }
        sbForHeight.append(HTemplate.replace("{0}", portion + "").replace("{1}",
                h + ""));
        sbForHeight.append("</resources>");

        /**
         *{0}-HEIGHT
         *{1}-WIDTH
         */
        final String VALUE_TEMPLATE = "values-{0}x{1}";
        File fileDir = new File(dirStr + File.separator
                + VALUE_TEMPLATE.replace("{0}", h + "")//
                .replace("{1}", w + ""));
        fileDir.mkdir();

        File layout_wFile = new File(fileDir.getAbsolutePath(), "layout_w.xml");
        File layout_hFile = new File(fileDir.getAbsolutePath(), "layout_h.xml");
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(layout_wFile));
            pw.print(sbForWidth.toString());
            pw.close();
            pw = new PrintWriter(new FileOutputStream(layout_hFile));
            pw.print(sbForHeight.toString());
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * correct to two decimal places
     *
     * @param a param
     * @return result
     */
    public static float change(float a) {
        int temp = (int) (a * 100);
        return temp / 100f;
    }

    public static void main(String[] args) {
        String addition = "";
        try {
            if (args.length >= 1) {
                addition = args[0];
            }
        } catch (NumberFormatException e) {

            System.err
                    .println("right input params : java -jar xxx.jar width height w,h_w,h_..._w,h;");
            e.printStackTrace();
            System.exit(-1);
        }

        new GenerateValueFiles(addition).generate();
    }
}
