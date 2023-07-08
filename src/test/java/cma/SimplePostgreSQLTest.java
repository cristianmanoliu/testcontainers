package cma;

import static org.assertj.core.api.Assertions.assertThat;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class SimplePostgreSQLTest {

  @Test
  public void testSimple() throws SQLException {
    try (PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>(DockerImageName.parse("postgres:9.6.12"))) {
      postgres.start();

      ResultSet resultSet = performQuery(postgres, "SELECT 1");
      int resultSetInt = resultSet.getInt(1);
      assertThat(resultSetInt).as("A basic SELECT query succeeds").isEqualTo(1);
      assertHasCorrectExposedAndLivenessCheckPorts(postgres);
    }
  }

  private void assertHasCorrectExposedAndLivenessCheckPorts(PostgreSQLContainer<?> postgres) {
    assertThat(postgres.getExposedPorts()).containsExactly(PostgreSQLContainer.POSTGRESQL_PORT);
    assertThat(postgres.getLivenessCheckPortNumbers())
        .containsExactly(postgres.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT));
  }

  protected ResultSet performQuery(JdbcDatabaseContainer<?> container, String sql)
      throws SQLException {
    DataSource ds = getDataSource(container);
    Statement statement = ds.getConnection().createStatement();
    statement.execute(sql);
    ResultSet resultSet = statement.getResultSet();
    resultSet.next();
    return resultSet;
  }

  protected DataSource getDataSource(JdbcDatabaseContainer<?> container) {
    HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setJdbcUrl(container.getJdbcUrl());
    hikariConfig.setUsername(container.getUsername());
    hikariConfig.setPassword(container.getPassword());
    hikariConfig.setDriverClassName(container.getDriverClassName());
    return new HikariDataSource(hikariConfig);
  }
}
