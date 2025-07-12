package com.example.siaj_mobile;

import java.math.BigDecimal;

public class VentaDTO {
    private int id;
    private BigDecimal total;
    private String estado;
    private String fechaPago;
    private UsuarioDTO usuarioDTO;
    private MedioPago medioPago;

    public int getId() { return id; }
    public BigDecimal getTotal() { return total; }
    public String getEstado() { return estado; }
    public String getFechaPago() { return fechaPago; }
    public UsuarioDTO getUsuarioDTO() { return usuarioDTO; }
    public MedioPago getMedioPago() { return medioPago; }
}
