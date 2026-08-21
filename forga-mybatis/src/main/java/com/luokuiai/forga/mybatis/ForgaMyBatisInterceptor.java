package com.luokuiai.forga.mybatis;

import java.lang.reflect.Field;
import java.util.Objects;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * MyBatis plugin that applies configured Forga authorization constraints to SELECT statements.
 */
@Intercepts({
    @Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public final class ForgaMyBatisInterceptor implements Interceptor {

  private final MyBatisAuthorizationSqlInterceptor sqlInterceptor;

  /**
   * Creates a MyBatis interceptor.
   *
   * @param sqlInterceptor framework-neutral SQL interception support
   */
  public ForgaMyBatisInterceptor(MyBatisAuthorizationSqlInterceptor sqlInterceptor) {
    this.sqlInterceptor = Objects.requireNonNull(sqlInterceptor, "sqlInterceptor is required");
  }

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    Object[] args = invocation.getArgs();
    MappedStatement statement = (MappedStatement) args[0];
    Object parameter = args[1];
    BoundSql boundSql = statement.getBoundSql(parameter);
    MyBatisBoundSql authorized = sqlInterceptor.intercept(statement.getId(), boundSql.getSql());
    replaceSql(boundSql, authorized.sql());
    bindParameters(boundSql, authorized);
    return invocation.proceed();
  }

  @Override
  public Object plugin(Object target) {
    return Plugin.wrap(target, this);
  }

  private static void replaceSql(BoundSql boundSql, String sql) {
    try {
      Field field = BoundSql.class.getDeclaredField("sql");
      field.setAccessible(true);
      field.set(boundSql, sql);
    } catch (ReflectiveOperationException exception) {
      throw new MyBatisAuthorizationException("failed to rewrite MyBatis SQL");
    }
  }

  private static void bindParameters(BoundSql boundSql, MyBatisBoundSql authorized) {
    authorized.parameterValues()
        .forEach(
            (name, value) -> boundSql.setAdditionalParameter("forga.parameters." + name, value));
  }
}
