package cl.usach.fitcollab.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.usach.fitcollab.entities.SolicitudModificacion;
import cl.usach.fitcollab.services.SolicitudModificacionService;

@RestController
@RequestMapping("/api/solicitudes")
@CrossOrigin(origins = "*")
public class SolicitudModificacionController {
     private final SolicitudModificacionService solicitudService;

     public SolicitudModificacionController(SolicitudModificacionService solicitudService){
         this.solicitudService = solicitudService;
     }

     //ver solicitudes pendientes de dieta
     @GetMapping("/dieta/pendientes")
    public ResponseEntity<?> obtenerSolicitudesPendientesDieta(){
         try{
             List<SolicitudModificacion> solicitudes = solicitudService.obtenerSolicitudesPendientesDieta();
             return ResponseEntity.ok(solicitudes);
     } catch (Exception e) {
             return ResponseEntity.internalServerError().body("Error al obtener las solicitudes.");
         }
    }

    @GetMapping("/deportista/{deportistaId}")
    public ResponseEntity<?> obtenerSolicitudesPorDeportista(@PathVariable Long deportistaId){
         try {
             List<SolicitudModificacion> solicitudes = solicitudService.obtenerSolicitudesPorDeportista(deportistaId);
             return ResponseEntity.ok(solicitudes);
         } catch (Exception e){
             return ResponseEntity.internalServerError().body("Error al obtener las solicitudes.");
         }
     }

     @PutMapping("/{id}/responder")
    public ResponseEntity<?> responderSolicitud(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, String> payload){
         try {
             String estado = payload.get("estado");
             if (estado == null || estado.trim().isEmpty()){
                 return ResponseEntity.badRequest().body("Debe indicar el estado de la respuesta.");
             }
             SolicitudModificacion respondida = solicitudService.responderSolicitud(id, estado);
             return ResponseEntity.ok(respondida);
         } catch (RuntimeException e){
             return ResponseEntity.badRequest().body(e.getMessage());
         } catch (Exception e){
             return ResponseEntity.internalServerError().body("Error al responder la solicitud.");
         }
     }

     //crear solicitud de modificacion de dieta
    @PostMapping("/dieta")
     public ResponseEntity<?> crearSolicitudDieta(@RequestBody SolicitudModificacion solicitud){
         try {
             if (solicitud.getMotivo() == null || solicitud.getMotivo().trim().isEmpty()){
                 return ResponseEntity.badRequest().body("Debe completar el motivo de la solicitud.");
             }
             SolicitudModificacion nueva = solicitudService.crearSolicitudDieta(solicitud);
             return ResponseEntity.ok(nueva);
         } catch  (IllegalStateException e){
             return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e){
             return ResponseEntity.internalServerError().body("Error al crear la solicitud.");
         }
    }
}
