package com.n2.kafkaProducer;

import static org.apache.avro.SchemaCompatibility.SchemaPairCompatibility;
import static org.apache.avro.SchemaCompatibility.checkReaderWriterCompatibility;
import static org.assertj.core.api.Java6Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Parser;
import org.apache.avro.SchemaCompatibility.SchemaCompatibilityType;
import org.junit.Test;

public class InterestEventCompatibilityTest {

  @Test
  public void testV1SchemaForwardCompatibilityWithLatestSchema() throws IOException {
    //given
    Schema v1Schema = new Parser().parse(getFile("MyEvent_v1.avsc"));
    Schema latestSchema = new Parser().parse(getNewSchemaFile());
    //when
    SchemaPairCompatibility compatibilityResult = checkReaderWriterCompatibility(v1Schema, latestSchema);
    //then
    assertThat(compatibilityResult.getType()).isEqualTo(SchemaCompatibilityType.COMPATIBLE);
  }

  @Test
  public void testNewSchemaBackwardCompatibilityWithV1Schema() throws IOException {
    //given
    Schema v1Schema = new Parser().parse(getFile("MyEvent_v1.avsc"));
    Schema newSchema = new Parser().parse(getNewSchemaFile());
    //when
    SchemaPairCompatibility compatibilityResult = checkReaderWriterCompatibility(newSchema, v1Schema);
    //then
    assertThat(compatibilityResult.getType()).isEqualTo(SchemaCompatibilityType.COMPATIBLE);
  }

  private File getFile(final String fileName) {
    return new File("src/test/resources/" + fileName);
  }

  private File getNewSchemaFile() {
    return new File("src/main/avro/MyEvent_v2.avsc");
  }
}