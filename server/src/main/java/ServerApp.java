import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class ServerApp {

    static Logger logger = Logger.getLogger(ServerApp.class);

    private final int PORT;

    public ServerApp(int PORT) {
        BasicConfigurator.configure();
        this.PORT = PORT;
    }

    private void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sh) throws Exception {
                            sh.pipeline().addLast(new StringEncoder());
                            sh.pipeline().addLast(new LineBasedFrameDecoder(80));
                            sh.pipeline().addLast(new StringDecoder());
                            sh.pipeline().addLast(new ServerHandlerShowStringMsg());
                        }
                    });

            ChannelFuture chf = sb.bind(PORT).sync();
            System.out.println("Started");
            chf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new ServerApp(8189).run();
    }
}
