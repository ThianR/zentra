# Arquitectura y Stack Tecnologico - Zentra

## 1. Definicion del Sistema
- Tipo: Monolito Modular SaaS Multi-tenant.
- Enfoque: Middleware de Integracion para Facturacion Electronica SIFEN Paraguay.

## 2. Stack Tecnico Obligatorio
- Backend: Java 21 LTS, Spring Boot 3.4+, Spring Data JPA.
- Integracion: Apache CXF (SOAP), JAXB (XML), Resilience4j.
- Base de Datos: PostgreSQL 16+, Redis (Cache/Idempotencia).
- Documental: MinIO o S3 compatible.
- Frontend: React 18, TypeScript, Tailwind CSS, shadcn/ui.

## 3. Estructura de Modulos
- modulo-core: Modelos canonicos y logica de negocio comun.
- modulo-crypto: Gestion de certificados y firma XMLDSig.
- modulo-xml: Generacion y validacion de esquemas XSD.
- modulo-sifen: Clientes SOAP, mTLS y gestion de transmision.
- modulo-kude: Generacion de representacion grafica (PDF/QR).
- modulo-api: Exposicion REST y seguridad.
- modulo-tenant: Gestion de empresas y configuracion por cliente.
- modulo-integraciones: Conectores externos y ETL.
