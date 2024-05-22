package br.com.zalf.prologappreports;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@Builder(toBuilder = true, setterPrefix = "with")
public class HttpRequestCreateObject {
    @NotNull
    private final String requestName;
    @NotNull
    private final String domainName;
    @NotNull
    private final String path;
    @NotNull
    private final String httpMethod;
    private final int port;
    @NotNull
    private final String protocol;
    @Nullable
    private final String headerName;
    @Nullable
    private final String headerValue;
}
