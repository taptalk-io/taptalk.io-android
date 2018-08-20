package com.moselo.HomingPigeon.Helper.AESCrypto;

public class JsFunctionCallFormatter {
    public JsFunctionCallFormatter() {
    }

    public static String paramToString(Object param) {
        String str = "";
        if (param instanceof String) {
            str = (String)param;
            str = str.replace("\\", "\\\\");
            str = str.replace("\"", "\\\"");
            str = str.replace("\n", "\\n");
            str = String.format("\"%s\"", str);
        } else {
            try {
                double d = Double.parseDouble(param.toString());
                str = param.toString();
            } catch (NumberFormatException var4) {
                ;
            }
        }

        return str;
    }

    public static String toString(String functionName, Object... args) {
        StringBuilder paramsStr = new StringBuilder();
        Object[] var3 = args;
        int var4 = args.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Object param = var3[var5];
            if (paramsStr.length() > 0) {
                paramsStr.append(", ");
            }

            paramsStr.append(paramToString(param));
        }

        return String.format("%s(%s)", functionName, paramsStr);
    }
}
