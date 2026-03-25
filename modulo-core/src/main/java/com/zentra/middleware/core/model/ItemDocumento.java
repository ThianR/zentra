package com.zentra.middleware.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "documentos_items")
public class ItemDocumento {

    @Id
    private String id = UUID.randomUUID().toString();

    @ManyToOne
    private DocumentoElectronico documento;

    private String codigo;
    private String descripcion;
    private String unidadMedida = "UNI"; // Ej: 77 UNIDAD
    private Integer cantidad;
    private Double precioUnitario;
    private Double tasaIva = 10.0; // 5, 10, o 0
    private Double montoDescuento = 0.0;
    
    // Calculados
    private Double montoTotalItem = 0.0;
    private Double montoIvaItem = 0.0;

    public ItemDocumento() {}

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public DocumentoElectronico getDocumento() { return documento; }
    public void setDocumento(DocumentoElectronico documento) { this.documento = documento; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }
    public Double getTasaIva() { return tasaIva; }
    public void setTasaIva(Double tasaIva) { this.tasaIva = tasaIva; }
    public Double getMontoDescuento() { return montoDescuento; }
    public void setMontoDescuento(Double montoDescuento) { this.montoDescuento = montoDescuento; }
    public Double getMontoTotalItem() { return montoTotalItem; }
    public void setMontoTotalItem(Double montoTotalItem) { this.montoTotalItem = montoTotalItem; }
    public Double getMontoIvaItem() { return montoIvaItem; }
    public void setMontoIvaItem(Double montoIvaItem) { this.montoIvaItem = montoIvaItem; }
}
