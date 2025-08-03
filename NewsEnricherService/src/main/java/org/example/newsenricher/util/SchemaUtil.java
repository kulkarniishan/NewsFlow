package org.example.newsenricher.util;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

public class SchemaUtil {
    public static StructType getNewsSchema() {
        return new StructType()
                .add("title", DataTypes.StringType)
                .add("link", DataTypes.StringType)
                .add("sourceId", DataTypes.StringType)
                .add("publishedAt", DataTypes.StringType)
                .add("description", DataTypes.StringType);
    }
}
