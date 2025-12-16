/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.Containers.Dependency;

import com.WT2.ImpresonesFE.application.service.GestionarFeTirillaImpresion;
import com.WT2.ImpresonesFE.application.service.EncolarPeticionFeImprimir;
import com.WT2.ImpresonesFE.application.useCases.EliminarDeCola;
import com.WT2.ImpresonesFE.application.useCases.EnviarFeImprimir;
import com.WT2.ImpresonesFE.application.useCases.ValidaPeticionFeEncolada;
import com.WT2.ImpresonesFE.domain.entities.ParametrosPeticionFePrinter;
import com.WT2.ImpresonesFE.domain.entities.PeticionFeImprimir;
import com.WT2.ImpresonesFE.domain.entities.RespuestaFeImprimir;
import com.WT2.ImpresonesFE.presentation.agentes.AgenteDeImpresionFE;
import com.WT2.appTerpel.application.UseCase.CheckIsLoyaltyProcessExist;
import com.WT2.appTerpel.application.UseCase.ConvertNotificationPaymentToJsonObject;
import com.WT2.appTerpel.application.UseCase.CreateLoyaltiProcess;
import com.WT2.appTerpel.application.UseCase.DefineViewToAlert;
import com.WT2.appTerpel.application.UseCase.EvaluarIntentosPagosAppterpel;
import com.WT2.appTerpel.application.UseCase.Payment.Listing.RecoverDataMedioPago;
import com.WT2.appTerpel.application.UseCase.Payment.Listing.RecoverPaymentProcessedAppterpel;
import com.WT2.appTerpel.application.UseCase.Payment.Procesing.ValidateIfDeleteAppTerpelPayment;
import com.WT2.appTerpel.application.UseCase.Payment.Procesing.ValidateIsAppTerpelPaymentSuccess;
import com.WT2.appTerpel.application.UseCase.RecoverMedioPagoImage;
import com.WT2.appTerpel.application.UseCase.RecoverMedioPagoWithoutGoPass;
import com.WT2.appTerpel.application.UseCase.RecuperarTiempoMensajeAppterpel;
import com.WT2.appTerpel.application.UseCase.UpdateImagenReferenciaMedioPago;
import com.WT2.appTerpel.application.UseCase.ValidarBotonesVentasAppterpel;
import com.WT2.appTerpel.application.service.SendToMovimientoMedioPago;
import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.appTerpel.commons.dto.MedioPagoInTableViewDto;
import com.WT2.appTerpel.commons.dto.PaymentDTO;
import com.WT2.appTerpel.commons.dto.SalesDto;
import com.WT2.appTerpel.commons.mappers.MedioPagoInTableViewDtoMapper;
import com.WT2.appTerpel.commons.mappers.MedioPagoInTableViewMapper;
import com.WT2.appTerpel.commons.mappers.MedioPagoInTableViewMapperList;
import com.WT2.appTerpel.commons.mappers.PaymentMapper;
import com.WT2.appTerpel.commons.mappers.SalesDtoMapper;
import com.WT2.appTerpel.commons.mappers.SalesMapper;
import com.WT2.appTerpel.commons.service.AddIdMovimientoToMedioPagoView;
import com.WT2.commons.domain.adapters.IHttpClientRepository;
import com.WT2.appTerpel.domain.entities.MedioPagoInTableView;
import com.WT2.appTerpel.domain.entities.Payment;
import com.WT2.appTerpel.domain.entities.Sales;
import com.WT2.appTerpel.domain.entities.VentasAppterpelBotonesValidador;
import com.WT2.commons.infraestructure.repository.HttpClientRepository;
import com.WT2.payment.infrastructure.db.repositories.PaymentRepository;
import com.WT2.appTerpel.infraestructura.Repository.SalesAppTerpelProcessRepository;
import com.WT2.appTerpel.infraestructura.Repository.ValidarBotonesVentasAppterpelRepository;
import com.WT2.appTerpel.infraestructura.Repository.ValidarIntentoAppTerpelRepository;
import com.WT2.appTerpel.infraestructura.mapperJson.GsonMapper;
import com.WT2.appTerpel.presentation.SendPaymentAppTerpelToEndPointHandler;
import com.WT2.commons.application.UseCase.RecuperarParameterFidelizacion;
import com.WT2.commons.domain.adapters.IConnectionDB;
import com.WT2.commons.domain.adapters.IRepository;
import com.WT2.commons.domain.adapters.IUseCase;
import com.WT2.commons.domain.entity.Parameter;
import com.WT2.commons.domain.entity.ValoresParametros;
import com.WT2.commons.domain.entity.WatcherParameter;
import com.WT2.commons.infraestructure.db.Repository.ConnectionDB;
import com.WT2.commons.infraestructure.db.Repository.CrearProcesoFidelizacionSinData;
import com.WT2.commons.infraestructure.db.Repository.CrearSinDatoFidelizacionRepository;
import com.WT2.commons.infraestructure.db.Repository.ParametersRepository;
import com.WT2.commons.infraestructure.db.Repository.ParametrosParametrizacionRepository;
import com.WT2.commons.infraestructure.db.Repository.RepositoryTransaccionProceso;
import com.WT2.commons.infraestructure.db.Repository.WacherParemeterRepository;
import com.WT2.glp.application.UseCase.ValidaterUrlSicom;
import com.WT2.goPass.application.useCase.ConsultarPlacaStatusPum;
import com.WT2.goPass.application.useCase.ConsultarPlacasModuloGoPass;
import com.WT2.goPass.application.useCase.ObtenerParametrosGopass;
import com.WT2.goPass.application.useCase.ProcesarPagoGopass;
import com.WT2.goPass.domain.entity.beans.GopassParameters;
import com.WT2.goPass.domain.entity.request.ConsultarPlacaStatusPumRequestBody;
import com.WT2.goPass.domain.entity.response.ConsultarPlacasResponse;
import com.WT2.goPass.infrastructure.controllers.ConsultarPlacasModuloController;
import com.WT2.goPass.infrastructure.controllers.ConsultarPlacasStatusPumController;
import com.WT2.goPass.infrastructure.db.repositories.GoPassCarPlateRepository;
import com.WT2.goPass.infrastructure.db.repositories.GopassParametersRepository;
import com.WT2.goPass.infrastructure.services.GopassParametersService;
import com.WT2.loyalty.application.Service.AcumulacionAutomatica;
import com.WT2.loyalty.application.Service.AcumularPuntosService;
import com.WT2.loyalty.application.UseCase.AcumularPuntosRequestEndpoint;
import com.WT2.loyalty.application.UseCase.ConsultarClienteEndpointTerpel;
import com.WT2.loyalty.application.UseCase.RedimirBono;
import com.WT2.loyalty.domain.entities.request.ConsultClientRequestBody;
import com.WT2.loyalty.domain.entities.beans.LoyalityConfig;
import com.WT2.loyalty.domain.entities.params.ParamsAcumularLoyalty;
import com.WT2.loyalty.infrastructure.controllers.AcumularPuntosController;
import com.WT2.loyalty.presentation.dto.ConsultClientRequestBodyDto;
import com.WT2.loyalty.presentation.dto.ParamsAcumularLoyaltyDto;
import com.WT2.loyalty.presentation.mapper.ConsultClientRequestBodyMapper;
import com.WT2.loyalty.presentation.mapper.QueryClientRequestBodyDtoMapper;
import com.WT2.loyalty.presentation.mapper.LoyaltyConfigMapper;
import com.WT2.loyalty.presentation.mapper.ParamsAcumularLoyaltyDtoMapper;
import com.WT2.loyalty.presentation.mapper.ParamsAcumularLoyaltyMapper;
import com.WT2.payment.application.usesCase.EnviandoMedioPago;
import com.WT2.payment.domian.entities.PaymentResponse;
import com.WT2.payment.infrastructure.controllers.ProcessingPaymentController;
import com.WT2.turns.application.useCase.ValidateBusinessType;
import com.WT2.turns.application.useCase.ValidateModeWithoutPump;
import com.dao.SetupDao;
import com.firefuel.Main;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author USUARIO
 */
public class DependencyInjection {

    private RecoverDataMedioPago recoverMedio;
    private RecoverMedioPagoImage medioPago;
    private RecoverMedioPagoWithoutGoPass medioPagoWithoutGoPass;
    private UpdateImagenReferenciaMedioPago uirmp;
    private ConvertNotificationPaymentToJsonObject notifyPaymentAlert;
    private DefineViewToAlert defineViewToAlert;
    private IMapper<Vector<Vector>, List<MedioPagoInTableViewDto>> medioPagoInTableViewDtoMapper;
    private IMapper<MedioPagoInTableViewDto, MedioPagoInTableView> medioPagoInTableViewMapper;
    private IMapper<List<MedioPagoInTableViewDto>, List<MedioPagoInTableView>> medioPagoInTableViewListMapper;
    private IMapper<PaymentDTO, Payment> paymentMapper;
    private IMapper<SalesDto, Sales> salesMapper;
    private IMapper<ResultSet, SalesDto> salesDtoMapper;
    private IMapper<Map<String, String>, ConsultClientRequestBodyDto> consultClientRequstBodyDtoMapper;
    private IMapper<ConsultClientRequestBodyDto, ConsultClientRequestBody> consultClientRequestBodyMapper;
    private IMapper<String, LoyalityConfig> loyaltyConfigMapper;
    private IMapper<Map<String, Object>, ParamsAcumularLoyaltyDto> paramsAcumularLoyaltyMapperDto;
    private IMapper<ParamsAcumularLoyaltyDto, ParamsAcumularLoyalty> paramsAcumularLoyaltyMapper;
    private IHttpClientRepository httpClient;
    private IRepository<Long, Payment> paymentRepository;
    private IRepository<String, WatcherParameter> watcherParameterRepo;
    private AddIdMovimientoToMedioPagoView addIdMovimientoToMedioPagoView;
    private Connection connDB = Main.obtenerConexion("lazoexpresscore");
    private ValidateIfDeleteAppTerpelPayment validatePaymentStatus;
    private ValidateIsAppTerpelPaymentSuccess validateIsAppTerpelPaymentProcessed;
    private RecoverPaymentProcessedAppterpel recoverPaymentProcessedAppterpel;
    private SalesAppTerpelProcessRepository salesAppTerpelProcessRepository;
    private ConsultarClienteEndpointTerpel consultarCliente;
    private AcumulacionAutomatica acumulacionAutomatica;
    private RecuperarParameterFidelizacion recuperarParameter;
    private AcumularPuntosController acumularPuntosController;
    private AcumularPuntosRequestEndpoint acumularPuntosRequestEndpoint;
    private AcumularPuntosService acumularPuntosService;
    private ConsultarPlacasModuloGoPass consultarPlacasModuloGoPass;
    private IUseCase<ConsultarPlacaStatusPumRequestBody, ConsultarPlacasResponse> consultarPlacaStatusPum;
    private ConsultarPlacasModuloController consultarPlacasModuloController;
    private ConsultarPlacasStatusPumController consultarPlacasStatusPumController;
    private final EnviandoMedioPago envioMedioPago;
    private ProcessingPaymentController processingPaymentController;
    private CreateLoyaltiProcess createLoyaltiProcess;
    private CrearSinDatoFidelizacionRepository crearProcesoFidelizacionRepository;
    private RepositoryTransaccionProceso repositoryTransaccionProceso;
    private CrearProcesoFidelizacionSinData crearProcesoFidelizacionSinData;
    private CheckIsLoyaltyProcessExist checkIsLoyaltyProcessExist;
    private GoPassCarPlateRepository goPassCarPlateRepository;
    private GopassParametersRepository gopassParametersRepository;
    private GopassParametersService gopassParametersService;
    private ProcesarPagoGopass procesarPagoGopass;
    private IUseCase<Void, Boolean> validateModeWithoutPump;
    private IUseCase<Void, String> validateBusinessType;
    private IConnectionDB<Connection> connectionDB;
    private IRepository<String, Parameter> ParametersRepo;
    private IRepository<String, ValoresParametros> parametroParametrizacionRepo;
    private IRepository<Long, VentasAppterpelBotonesValidador> validarBotonesVentasAppterpelRepo;
    private IUseCase<Long, VentasAppterpelBotonesValidador> validarBotonesVentasAppterpel;
    private IRepository<Long, Integer> validarIntentosRepo;
    private IUseCase<Void, String> validateUrlGlp;
    private IUseCase<ParametrosPeticionFePrinter, Boolean> encolarPeticionFeImprimir;
    private IUseCase<ParametrosPeticionFePrinter, RespuestaFeImprimir> enviarPeticionFeImprimir;
    private GestionarFeTirillaImpresion GestionarFeTirillaImpresion;
    private AgenteDeImpresionFE agenteDeImpresionFE;
    private IUseCase<PeticionFeImprimir, Boolean> validaPeticionFeEncolada;
    private IUseCase<ParametrosPeticionFePrinter, Boolean> eliminarEncolada;
    private IUseCase<Void, Integer> RecuperarTiempoMensajeAppterpel;
    private IUseCase<Long, Boolean> evaluarIntentosPagoAppterpel;
    private IUseCase<Void, GopassParameters> recuperarParametrosGopass;
    private IUseCase<List<MedioPagoInTableView>, PaymentResponse> sendToMovimientoMedioPago;
    private SendPaymentAppTerpelToEndPointHandler sendPaymentAppTerpelToEndPointHandler;
    
    private RedimirBono redimirBono;

    @SuppressWarnings("unchecked")

    public DependencyInjection(SetupDao setupDao) {
        this.connectionDB = new ConnectionDB("lazoexpresscore");
        try {
            this.httpClient = new HttpClientRepository<>();

        } catch (IOException ex) {
            Logger.getLogger(DependencyInjection.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.parametroParametrizacionRepo = new ParametrosParametrizacionRepository(connectionDB);
        this.consultarCliente = new ConsultarClienteEndpointTerpel(this.httpClient);
        this.consultClientRequstBodyDtoMapper = new QueryClientRequestBodyDtoMapper();
        this.consultClientRequestBodyMapper = new ConsultClientRequestBodyMapper();
        this.salesDtoMapper = new SalesDtoMapper();
        this.salesMapper = new SalesMapper();
        this.paymentRepository = new PaymentRepository(connectionDB);
        this.validarBotonesVentasAppterpelRepo = new ValidarBotonesVentasAppterpelRepository(connectionDB);
        this.validarBotonesVentasAppterpel = new ValidarBotonesVentasAppterpel(this.validarBotonesVentasAppterpelRepo);
        this.watcherParameterRepo = new WacherParemeterRepository(connDB);
        this.paymentMapper = new PaymentMapper();
        this.recoverMedio = new RecoverDataMedioPago(setupDao);
        this.medioPago = new RecoverMedioPagoImage();
        this.uirmp = new UpdateImagenReferenciaMedioPago(setupDao);
        this.medioPagoWithoutGoPass = new RecoverMedioPagoWithoutGoPass();
        this.notifyPaymentAlert = new ConvertNotificationPaymentToJsonObject(new GsonMapper());
        this.defineViewToAlert = new DefineViewToAlert();
        this.medioPagoInTableViewDtoMapper = new MedioPagoInTableViewDtoMapper();
        this.medioPagoInTableViewMapper = new MedioPagoInTableViewMapper();
        this.medioPagoInTableViewListMapper = new MedioPagoInTableViewMapperList();
        this.addIdMovimientoToMedioPagoView = new AddIdMovimientoToMedioPagoView();
        this.validatePaymentStatus = new ValidateIfDeleteAppTerpelPayment(paymentRepository);
        this.validateIsAppTerpelPaymentProcessed = new ValidateIsAppTerpelPaymentSuccess(paymentRepository);
        this.salesAppTerpelProcessRepository = new SalesAppTerpelProcessRepository(connDB, this.salesDtoMapper, salesMapper);
        this.recoverPaymentProcessedAppterpel = new RecoverPaymentProcessedAppterpel(salesAppTerpelProcessRepository);
        this.loyaltyConfigMapper = new LoyaltyConfigMapper();
        this.recuperarParameter = new RecuperarParameterFidelizacion(this.watcherParameterRepo, this.loyaltyConfigMapper);
        this.acumularPuntosRequestEndpoint = new AcumularPuntosRequestEndpoint(httpClient);
        this.acumularPuntosService = new AcumularPuntosService(acumularPuntosRequestEndpoint);
        this.paramsAcumularLoyaltyMapperDto = new ParamsAcumularLoyaltyDtoMapper();
        this.paramsAcumularLoyaltyMapper = new ParamsAcumularLoyaltyMapper();
        this.consultarPlacasModuloGoPass = new ConsultarPlacasModuloGoPass(httpClient);
        this.acumularPuntosController = new AcumularPuntosController(this.acumularPuntosService);
        this.consultarPlacasModuloController = new ConsultarPlacasModuloController(consultarPlacasModuloGoPass);
        this.consultarPlacaStatusPum = new ConsultarPlacaStatusPum(httpClient);
        this.consultarPlacasStatusPumController = new ConsultarPlacasStatusPumController(this.consultarPlacaStatusPum);
        this.envioMedioPago = new EnviandoMedioPago(httpClient);
        this.processingPaymentController = new ProcessingPaymentController();
        this.crearProcesoFidelizacionRepository = new CrearSinDatoFidelizacionRepository(connDB);
        this.repositoryTransaccionProceso = new RepositoryTransaccionProceso(connectionDB);
        this.crearProcesoFidelizacionSinData = new CrearProcesoFidelizacionSinData(connDB);
        this.createLoyaltiProcess = new CreateLoyaltiProcess(crearProcesoFidelizacionRepository, repositoryTransaccionProceso, crearProcesoFidelizacionSinData);
        this.checkIsLoyaltyProcessExist = new CheckIsLoyaltyProcessExist(repositoryTransaccionProceso);
        this.goPassCarPlateRepository = new GoPassCarPlateRepository(connectionDB);
        this.gopassParametersRepository = new GopassParametersRepository(connectionDB);
        this.gopassParametersService = new GopassParametersService(gopassParametersRepository);
        this.procesarPagoGopass = new ProcesarPagoGopass(httpClient, goPassCarPlateRepository, envioMedioPago);
        this.validateModeWithoutPump = new ValidateModeWithoutPump(watcherParameterRepo);
        this.validateBusinessType = new ValidateBusinessType(watcherParameterRepo);
        this.RecuperarTiempoMensajeAppterpel = new RecuperarTiempoMensajeAppterpel(parametroParametrizacionRepo);
        this.ParametersRepo = new ParametersRepository(connDB);
        this.validateUrlGlp = new ValidaterUrlSicom(ParametersRepo);
        this.validaPeticionFeEncolada = new ValidaPeticionFeEncolada();
        this.enviarPeticionFeImprimir = new EnviarFeImprimir(httpClient);
        this.encolarPeticionFeImprimir = new EncolarPeticionFeImprimir(validaPeticionFeEncolada);
        this.eliminarEncolada = new EliminarDeCola();
        this.GestionarFeTirillaImpresion = new GestionarFeTirillaImpresion(this.enviarPeticionFeImprimir, this.eliminarEncolada);
        this.agenteDeImpresionFE = new AgenteDeImpresionFE();
        this.validarIntentosRepo = new ValidarIntentoAppTerpelRepository(connectionDB);
        this.evaluarIntentosPagoAppterpel = new EvaluarIntentosPagosAppterpel(validarIntentosRepo);
        this.sendPaymentAppTerpelToEndPointHandler = new SendPaymentAppTerpelToEndPointHandler();
        this.sendToMovimientoMedioPago = new SendToMovimientoMedioPago();
        this.recuperarParametrosGopass = new ObtenerParametrosGopass(gopassParametersRepository, gopassParametersService);

        this.redimirBono = new RedimirBono(this.httpClient);
        
    }

    public RecuperarParameterFidelizacion getRecuperarParameter() {
        return recuperarParameter;
    }

    public RecoverMedioPagoWithoutGoPass getMedioPagoWithoutGoPass() {
        return medioPagoWithoutGoPass;
    }

    public UpdateImagenReferenciaMedioPago getUpdateImagenReferenciaMedioPago() {
        return uirmp;
    }

    public RecoverDataMedioPago getRecoverMedio() {
        return recoverMedio;
    }

    public RecoverMedioPagoImage getMedioPago() {
        return medioPago;
    }

    public ConvertNotificationPaymentToJsonObject getNotifyPaymentAlert() {
        return notifyPaymentAlert;
    }

    public DefineViewToAlert getDefineViewToAlert() {
        return defineViewToAlert;
    }

    public IMapper<Vector<Vector>, List<MedioPagoInTableViewDto>> getMedioPagoInTableViewDtoMapper() {
        return medioPagoInTableViewDtoMapper;
    }

    public IMapper<MedioPagoInTableViewDto, MedioPagoInTableView> getMedioPagoInTableViewMapper() {
        return medioPagoInTableViewMapper;
    }

    public IHttpClientRepository getHttpClient() {
        return httpClient;
    }

    public IMapper<List<MedioPagoInTableViewDto>, List<MedioPagoInTableView>> getMedioPagoInTableViewListMapper() {
        return medioPagoInTableViewListMapper;
    }

    public AddIdMovimientoToMedioPagoView getAddIdMovimientoToMedioPagoView() {
        return addIdMovimientoToMedioPagoView;
    }

    public IMapper<PaymentDTO, Payment> getPaymentMapper() {
        return paymentMapper;
    }

    public IRepository<Long, Payment> getPaymentRepository() {
        return paymentRepository;
    }

    public ValidateIfDeleteAppTerpelPayment getValidateIfDeleteAppTerpelPayment() {
        return validatePaymentStatus;
    }

    public ValidateIsAppTerpelPaymentSuccess getValidateIsAppTerpelPaymentProcessed() {
        return validateIsAppTerpelPaymentProcessed;
    }

    public RecoverPaymentProcessedAppterpel getRecoverPaymentProcessedAppterpel() {
        return recoverPaymentProcessedAppterpel;
    }

    public IMapper<Map<String, String>, ConsultClientRequestBodyDto> getConsultClientRequstBodyDtoMapper() {
        return consultClientRequstBodyDtoMapper;
    }

    public IMapper<ConsultClientRequestBodyDto, ConsultClientRequestBody> getConsultClientRequestBodyMapper() {
        return consultClientRequestBodyMapper;
    }

    public ConsultarClienteEndpointTerpel getConsultarCliente() {
        return consultarCliente;
    }

    public AcumulacionAutomatica getAcumulacionAutomatica() {
        return acumulacionAutomatica;
    }

    public AcumularPuntosController getAcumularPuntosController() {
        return acumularPuntosController;
    }

    public AcumularPuntosService getAcumularPuntosService() {
        return acumularPuntosService;
    }

    public IMapper<Map<String, Object>, ParamsAcumularLoyaltyDto> getParamsAcumularLoyaltyMapperDto() {
        return paramsAcumularLoyaltyMapperDto;
    }

    public IMapper<ParamsAcumularLoyaltyDto, ParamsAcumularLoyalty> getParamsAcumularLoyaltyMapper() {
        return paramsAcumularLoyaltyMapper;
    }

    public ConsultarPlacasModuloController getConsultarPlacasModuloController() {
        return consultarPlacasModuloController;
    }

    public EnviandoMedioPago getEnvioMedioPago() {
        return envioMedioPago;
    }

    public ProcessingPaymentController getProcessingPaymentController() {
        return processingPaymentController;
    }

    public CreateLoyaltiProcess getCreateLoyaltiProcess() {
        return createLoyaltiProcess;
    }

    public CrearSinDatoFidelizacionRepository getCrearProcesoFidelizacionRepository() {
        return crearProcesoFidelizacionRepository;
    }

    public CheckIsLoyaltyProcessExist getCheckIsLoyaltyProcessExist() {
        return checkIsLoyaltyProcessExist;
    }

    public GoPassCarPlateRepository getGoPassCarPlateRepository() {
        return goPassCarPlateRepository;
    }
    
    public GopassParametersRepository getGopassParametersRepository(){
        return gopassParametersRepository;
    }

    public ProcesarPagoGopass getProcesarPagoGopass() {
        return procesarPagoGopass;
    }

    public IUseCase<Void, Boolean> getValidateModeWithoutPump() {
        return validateModeWithoutPump;
    }

    public IUseCase<Void, String> getValidateBusinessType() {
        return validateBusinessType;
    }

    public IRepository<String, Parameter> getParametersRepo() {
        return ParametersRepo;
    }

    public IUseCase<Void, String> getValidateUrlGlp() {
        return validateUrlGlp;
    }

    public IUseCase<ParametrosPeticionFePrinter, Boolean> getEncolarPeticionFeImprimir() {
        return encolarPeticionFeImprimir;
    }
    
    public IUseCase<Void, GopassParameters> getParametrosGopass(){
        return recuperarParametrosGopass;
    }

    public GestionarFeTirillaImpresion getGestionarFeTirillaImpresion() {
        return GestionarFeTirillaImpresion;
    }

    public AgenteDeImpresionFE getAgenteDeImpresionFE() {
        return agenteDeImpresionFE;
    }

    public IUseCase<Void, Integer> getRecuperarTiempoMensajeAppterpel() {
        return RecuperarTiempoMensajeAppterpel;
    }

    public IUseCase<Long, Boolean> getEvaluarIntentosPagoAppterpel() {
        return evaluarIntentosPagoAppterpel;
    }

    public IUseCase<List<MedioPagoInTableView>, PaymentResponse> getSendToMovimientoMedioPago() {
        return sendToMovimientoMedioPago;
    }

    public SendPaymentAppTerpelToEndPointHandler getSendPaymentAppTerpelToEndPointHandler() {
        return sendPaymentAppTerpelToEndPointHandler;
    }

    public IUseCase<Long, VentasAppterpelBotonesValidador> getValidarBotonesVentasAppterpel() {
        return validarBotonesVentasAppterpel;
    }

    public IUseCase<Void, GopassParameters> getRecuperarParametrosGopass() {
        return recuperarParametrosGopass;
    }

    public RedimirBono getRedimirBono() {
        return redimirBono;
    }
       

}
