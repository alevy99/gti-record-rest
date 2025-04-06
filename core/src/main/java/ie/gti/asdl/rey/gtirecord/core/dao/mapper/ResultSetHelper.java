package ie.gti.asdl.rey.gtirecord.core.dao.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ResultSetHelper {

    private final Set<String> columnLabels;

    /**
     * Creates a helper based on the provided ResultSet,
     * extracting and caching all column labels (aliases or real names).
     *
     * @param rs ResultSet to build the helper from
     * @throws SQLException if an error occurs while reading metadata
     */
    public ResultSetHelper(ResultSet rs) throws SQLException {
        this.columnLabels = extractColumnLabels(rs);
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
}

