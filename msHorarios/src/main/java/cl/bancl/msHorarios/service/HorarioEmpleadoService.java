package cl.bancl.msHorarios.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cl.bancl.msHorarios.client.EmpleadoClient;
import cl.bancl.msHorarios.dto.DiasTrabajoDTO;
import cl.bancl.msHorarios.dto.EjecutivosDTO;
import cl.bancl.msHorarios.dto.HorarioEmpleadoDTO;
import cl.bancl.msHorarios.model.HorariosEmpleado;
import cl.bancl.msHorarios.repository.HorariosEmpleadoRepository;

@Service
public class HorarioEmpleadoService {

    @Autowired
    private HorariosEmpleadoRepository repoHorarios;

    @Autowired
    private EmpleadoClient empleadoClient;

    // guardar horario, pero antes validar que el empleado exista en otro ms
    public HorariosEmpleado guardarHorario(HorariosEmpleado horario){
        try{
            //primero valido que el empleado exista en otro ms antes de guardar
            empleadoClient.obtenerEjecutivosDTO(horario.getEmpleadoId());
            horario.setActivo(true);
            return repoHorarios.save(horario);
        }catch(feign.FeignException.NotFound e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El empleado no existe");
        }
    }

    // consultamos horario validando que exista
    public HorarioEmpleadoDTO obtenerHorarioConEmpleado(Integer empleadoId) {
        EjecutivosDTO empRemote;
        try {
            //llamamos los datos 
            empRemote = empleadoClient.obtenerEjecutivosDTO(empleadoId);
        } catch (feign.FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado en el sistema remoto.");
        }

        // busca los horarios activos en nuestra BD local
        List<HorariosEmpleado> listaLocal = repoHorarios.findByEmpleadoIdAndActivoTrue(empleadoId);

        // [CORRECCIÓN ACADÉMICA]: Si no hay horarios asignados, lanzamos un NO_CONTENT (204) o controlamos la falta de elementos
        if (listaLocal.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "El empleado no registra horarios activos.");
        }

        // mapeamos los días a nuestro DTO de salida
        List<DiasTrabajoDTO> diasDTO = listaLocal.stream().map(h -> {  
            DiasTrabajoDTO dto = new DiasTrabajoDTO();                 
            dto.setDiaSemana(h.getDiaSemana());
            dto.setHoraInicio(h.getHoraInicio());
            dto.setHoraFin(h.getHoraFin());
            return dto;
        }).collect(Collectors.toList()); 

        // Juntamos todo en la respuesta final
        HorarioEmpleadoDTO respuesta = new HorarioEmpleadoDTO();
        respuesta.setEmpleadoId(empleadoId);
        respuesta.setNombreCompleto(empRemote.getNombre() + " " + empRemote.getApellido());
        respuesta.setHorarios(diasDTO);

        return respuesta;
    }

    // metodo para desactivar un horario por licencia
    public HorariosEmpleado desactivarPorLicencia(Integer id, String motivo) {
        HorariosEmpleado horario = repoHorarios.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro de horario no encontrado."));
        
        horario.setActivo(false);
        horario.setObservaciones(motivo); // Guardamos la razón en tu campo observaciones
        // guardamos el horario actualizado
        return repoHorarios.save(horario);
    }

    // Obtener la lista de horarios usando el ID del empleado con su nombre real
    public List<HorariosEmpleado> buscarPorEmpleado(Integer empleadoId) {
        // buscamos todos los horarios asignados a este empleado en nuestra BD de horarios
        List<HorariosEmpleado> horarios = repoHorarios.findByEmpleadoId(empleadoId);
        
        // si el empleado tiene horarios, vamos al otro microservicio por su nombre
        if (!horarios.isEmpty()) {
            try {        
                EjecutivosDTO empleadoDto = empleadoClient.obtenerEjecutivosDTO(empleadoId);
                          
                String nombreReal = empleadoDto.getNombre();
                                   
                horarios.forEach(horario -> horario.setNombreEmpleado(nombreReal));
                
            } catch (Exception e) {
                // Si el microservicio de empleados está apagado o falla, le ponemos un aviso
                horarios.forEach(horario -> horario.setNombreEmpleado("Nombre no disponible (msEmpleados fuera de linea)"));
                          
                System.out.println("error al conectar con msEmpleados mediante Feign Client: " + e.getMessage());
            }
        }
        
        // Retornamos la lista ya procesada (si viene vacía, el controlador se encargará del 204 Content)
        return horarios;
    }
}