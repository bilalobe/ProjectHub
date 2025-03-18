@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@fortressAccessControl.hasPermission(authentication, T(com.projecthub.security.Permission).value)")
public @interface FortressSecured {
    String[] value();
}
