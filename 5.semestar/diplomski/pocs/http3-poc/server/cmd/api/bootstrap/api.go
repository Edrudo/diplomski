package bootstrap

import (
	"net"
	"net/http"
	"time"

	"http3-server-poc/cmd/api/config"
	"http3-server-poc/internal/api/controller"
	controllermappers "http3-server-poc/internal/api/controller/mappers"
	"http3-server-poc/internal/api/router"
	"http3-server-poc/internal/domain/services"
	"http3-server-poc/internal/infrastructure/filesystem"
	"http3-server-poc/internal/infrastructure/inmemorycache"

	"github.com/quic-go/quic-go"
	"github.com/quic-go/quic-go/http3"
	"github.com/quic-go/quic-go/quicvarint"
	"go.uber.org/zap"
)

func newController(
	imageStoringService controller.ImageStoringService,
	logger *zap.Logger,
) *controller.Controller {
	serverRequestMapper := controllermappers.NewServerRequestMapper()
	return controller.NewController(imageStoringService, logger, serverRequestMapper)
}

func newImageProcessingEngine(
	imageHashChan chan string,
	imagePartsRepository services.ImagePartsRepository,
	imageStore services.ImageStore,
) *services.ImageProcessingEngine {
	return services.NewImageProcessingEngine(imageHashChan, imagePartsRepository, imageStore)
}

func newHttp3Server(handler http.Handler) http3.Server {
	return http3.Server{
		Addr:      "localhost:4242",
		Port:      4242,
		TLSConfig: config.GenerateTLSConfig(),
		QuicConfig: &quic.Config{
			HandshakeIdleTimeout: time.Millisecond * time.Duration(config.Cfg.OuicConfig.HandshakeIdleTimeoutMs),
			MaxIdleTimeout:       time.Millisecond * time.Duration(config.Cfg.OuicConfig.MaxIdleTimeoutMs),
			RequireAddressValidation: func(addr net.Addr) bool {
				return false // for now, should whitelist our clients address
			},
			InitialStreamReceiveWindow:     quicvarint.Max,
			MaxStreamReceiveWindow:         quicvarint.Max,
			InitialConnectionReceiveWindow: quicvarint.Max,
			MaxConnectionReceiveWindow:     quicvarint.Max,
			KeepAlivePeriod:                time.Millisecond * time.Duration(config.Cfg.OuicConfig.KeepAlivePeriod),
			DisablePathMTUDiscovery:        false,
		},
		Handler: handler,
	}
}

func Api(logger *zap.Logger) http3.Server {
	defer logger.Sync() // flushes buffer, if any

	imagePartsRepository := inmemorycache.NewImagePartsRepository()
	imageStore := filesystem.NewImageStore()
	imageHashChan := make(chan string)
	imageProcessingEngine := newImageProcessingEngine(imageHashChan, imagePartsRepository, imageStore)
	go imageProcessingEngine.StartProcessing()

	imageStoringService := services.NewImageStoringService(imagePartsRepository, imageHashChan)

	controller := newController(imageStoringService, logger)

	handler, err := router.GenerateRoutingHandler(controller)
	if err != nil {
		panic(err)
	}

	return newHttp3Server(handler)
}
