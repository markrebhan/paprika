package com.mrebhan.paprika.internal;

import java.util.List;
import java.util.Map;

public interface SqlScripts {
    List<String> getCreateScripts();
    Map<Integer, List<String>> getUpgradeScripts();
    int getVersion();
    String getSelectQuery(String tableName);
}
