package unnamed.utils;

public class SneakyThrower {

    public static class DummyException extends RuntimeException {
        private static final long serialVersionUID = -6881686725000810113L;
    }

    public static class Thrower<T extends Throwable> {
        @SuppressWarnings("unchecked")
        public T sneakyThrow(Throwable exception) throws T {
            throw (T) exception;
        }
    }

    private static final Thrower<DummyException> HELPER = new Thrower<DummyException>();

    @SuppressWarnings("all")
    public static DummyException sneakyThrow(Throwable exception) {
        HELPER.sneakyThrow(exception);
        return null;
    }

    @SuppressWarnings("unused")
    public static <T extends Throwable> void sneakyThrows(Class<? extends T> cls) throws T {}
}