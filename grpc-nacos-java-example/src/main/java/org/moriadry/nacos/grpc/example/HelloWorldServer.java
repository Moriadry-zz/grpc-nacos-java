package org.moriadry.nacos.grpc.example;

import io.grpc.BindableService;
import org.moriadry.nacos.grpc.GrpcServer;
import org.moriadry.nacos.grpc.example.impl.GrpcTestServiceImpl;
import org.moriadry.nacos.grpc.utils.ConfigResult;
import org.moriadry.nacos.grpc.utils.NacosUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;

public class HelloWorldServer {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    GrpcServer server;

    private void start(BindableService[] bindableServices) {
        server = new GrpcServer();
        int port = ConfigResult.GRPC_PORT;
        URI uri = URI.create(ConfigResult.NACOS_URI);
        Properties properties = new Properties();
        properties = NacosUtils.buildNacosProperties(uri, properties);
        server.init(port, properties, bindableServices);
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
                                                 @Override
                                                 public void run() {
                                                     // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                                                     System.err.println("*** shutting down gRPC server since JVM is shutting down");
                                                     server.stop();
                                                     System.err.println("*** server shut down");
                                                 }
                                             });

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final HelloWorldServer server = new HelloWorldServer();
        server.start(new GrpcTestServiceImpl());
        server.server.blockUtilShutdown();
    }

}