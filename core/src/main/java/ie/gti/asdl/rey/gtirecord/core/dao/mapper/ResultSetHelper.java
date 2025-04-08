package ie.gti.asdl.rey.gtirecord.core.dao.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class ResultSetHelper {

    private final ResultSet resultSet;
    private final Set<String> columnLabels;

    /**
     * Creates a helper based on the provided ResultSet,
     * extracting and caching all column labels (aliases or real names).
     *
     * @param resultSet ResultSet to build the helper from
     * @throws SQLException if an error occurs while reading metadata
     */
    public ResultSetHelper(ResultSet resultSet) throws SQLException {
        this.resultSet = resultSet;
        this.columnLabels = extractColumnLabels(resultSet);
    }

    /**
     * Checks if the ResultSet contains a column with the given label (alias or name).
     *
     * @param label column label or alias (case-insensitive)
     * @return true if the column exists, false otherwise
     */
    public boolean hasColumn(String label) {
        return columnLabels.contains(label.toLowerCase());
    }

    // Extracts column labels (aliases) from the ResultSet metadata and stores them in lowercase
    private Set<String> extractColumnLabels(ResultSet rs) throws SQLException {
        Set<String> labels = new HashSet<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            labels.add(metaData.getColumnLabel(i).toLowerCase());
        }
        return labels;
    }

    public Integer getIntIfPresent(String label) throws SQLException {
        return hasColumn(label) ? resultSet.getInt(label) : null;
    }

    public String getStringIfPresent(String label) throws SQLException {
        return hasColumn(label) ? resultSet.getString(label) : null;
    }

    public Long getLongIfPresent(String label) throws SQLException {
        return hasColumn(label) ? resultSet.getLong(label) : null;
    }

    public Boolean getBooleanIfPresent(String label) throws SQLException {
        return hasColumn(label) ? resultSet.getBoolean(label) : null;
    }

//    public Date getDateIfPresent(String label) throws SQLException {
//        return hasColumn(label) ? resultSet.getDate(label) : null;
//    }

    public void setIntIfPresent(String label, Consumer<Integer> setter) throws SQLException {
        if (hasColumn(label)) {
            setter.accept(resultSet.getInt(label));
        }
    }

    public void setStringIfPresent(String label, Consumer<String> setter) throws SQLException {
        if (hasColumn(label)) {
            setter.accept(resultSet.getString(label));
        }
    }
}

