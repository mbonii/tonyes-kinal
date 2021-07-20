/*
	Programador: 
		Bonifasi de León, Marcos Daniel
        2019-139
        IN5AM
	Creación:
		20/03/2020
        Creacion de las entidades:
			1. TipoPlato
            2. Productos
            3. Platos
            4. Productos_has_Platos
            5. TipoEmpleado
            6. Empleados
            7. Empresas
            8. Presupuesto
            9. Servicios
            10. Servicios_has_Platos
            11. Servicios_has_Empleados
		Creación de los procedimientos almacenados por cada tabla:
			sp_Listar();
            sp_Agregar();
            sp_Actualizar();
            sp_Eliminar();
            sp_Buscar();
*/

-- ----------------------TAREA 1 ---------------------------

drop database if exists	DBTonysKinal2019139;
Create database DBTonysKinal2019139;

Use DBTonysKinal2019139;

-- Alter user 'root'@'localhost' identified with mysql_native_password by 'root';

#-----DDL----- 

Create table TipoPlato(
	codigoTipoPlato int auto_increment,
    descripcion varchar(100),
	primary key PK_codigoTipoPlato(codigoTipoPlato)
);

Create table Productos(
	codigoProducto int auto_increment,
    nombreProducto varchar(150) not null,
    cantidad int not null,
    primary key PK_codigoProducto (codigoProducto)
);

Create table Platos(
	codigoPlato int auto_increment,
    cantidad int not null,
    nombrePlato varchar(50) not null,
    descripcionPlato varchar(150) not null,
    precioPlato decimal(10,2) not null,
    codigoTipoPlato int not null,
    primary key PK_codigoPlato(codigoPlato),
    constraint FK_Platos_TipoPlato
		foreign key(codigoTipoPlato) references TipoPlato(codigoTipoPlato)
			on delete cascade
            on update cascade
);

Create table Productos_has_Platos(
	codigoProductos_has_Platos int auto_increment,
	codigoProducto int,
	codigoPlato int,
    primary key PK_codigoProductos_has_Platos(codigoProductos_has_Platos),
    constraint FK_Productos_has_Platos_Productos
		foreign key(codigoProducto) references Productos(codigoProducto)
			on delete cascade
            on update cascade,
	constraint FK_Productos_has_Platos_Platos
		foreign key(codigoPlato) references Platos(codigoPlato)
			on delete cascade
            on update cascade
);

Create table TipoEmpleado(
	codigoTipoEmpleado int auto_increment,
    descripcion varchar(100),
    primary key PK_codigoTipoEmpleado(codigoTipoEmpleado)
);

Create table Empleados(
	codigoEmpleado int auto_increment,
    numeroEmpleado int,
	apellidosEmpleado varchar(150) not null,
    nombresEmpleado varchar(150) not null,
    direccionEmpleado varchar(150) not null,
    telefonoContacto varchar(10) not null,
    gradoCocinero varchar(50),
	codigoTipoEmpleado int,
    primary key PK_codigoEmpleado(codigoEmpleado),
    constraint FK_Empleados_TipoEmpleado
		foreign key(codigoTipoEmpleado) references TipoEmpleado(codigoTipoEmpleado)
			on delete cascade
            on update cascade
);

Create table Empresas(
	codigoEmpresa int auto_increment,
    nombreEmpresa varchar(150) not null,
    direccion varchar(150) not null,
    telefono varchar(50) not null,
    primary key PK_codigoEmpresa(codigoEmpresa)
);

Create table Presupuesto(
	codigoPresupuesto int auto_increment,
    fechaSolicitud date not null,
    cantidadPresupuesto decimal(10,2) not null,
    codigoEmpresa int,
    primary key PK_codigoPresupuesto(codigoPresupuesto),
    constraint FK_Presupuesto_Empresas
		foreign key(codigoEmpresa) references Empresas(codigoEmpresa)
			on delete cascade
            on update cascade
);

Create table Servicios(
	codigoServicio int auto_increment,
    fechaServicio date not null,
    tipoServicio varchar(100) not null,
    horaServicio time not null,
    lugarServicio varchar(100) not null,
    telefonoContacto varchar(10) not null,
    codigoEmpresa int,
    primary key PK_codigoServicio(codigoServicio),
    constraint FK_Servicios_Empresa
		foreign key(codigoEmpresa) references Empresas(codigoEmpresa)
			on delete cascade
            on update cascade
);

Create table Servicios_has_Platos(
	codigoServicios_has_Platos int auto_increment,
	codigoServicio int,
    codigoPlato int,
    primary key PK_codigoServicios_has_Platos(codigoServicios_has_Platos),
    constraint FK_Servicios_has_Platos_Servicios
		foreign key(codigoServicio) references Servicios(codigoServicio)
			on delete cascade
            on update cascade,
    constraint FK_Servicios_has_Platos_Platos
		foreign key(codigoPlato) references Platos(codigoPlato)
			on delete cascade
            on update cascade
);

Create table Servicios_has_Empleados(
	codigoServicios_has_Empleados int auto_increment,
	codigoServicio int,
    codigoEmpleado int,
    fechaEvento date not null,
    horaEvento time not null,
    lugarEvento varchar(150) not null,
    primary key PK_codigoServicios_has_Empleados(codigoServicios_has_Empleados),
    constraint FK_Servicios_has_Empleados_Servicios
		foreign key(codigoServicio) references Servicios(codigoServicio)
			on delete cascade
            on update cascade,
	constraint FK_Servicios_has_Empleados_Empleados
		foreign key(codigoEmpleado) references Empleados(codigoEmpleado)
			on delete cascade
            on update cascade
);

-- ----------------------TAREA 2 -------------------------

#-----Procedimientos Almacenados-----

#Entidad: TipoPlato

Delimiter $$
	Create procedure sp_ListarTipoPlato()
		Begin
			select TipoPlato.codigoTipoPlato,
					TipoPlato.descripcion
                    from TipoPlato;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_AgregarTipoPlato(in descripcion varchar(1000))
		Begin
			insert into TipoPlato(descripcion) values(descripcion);
		End$$
Delimiter ;

Delimiter $$
	Create procedure sp_ActualizarTipoPlato(in codigo int, in descrip varchar(100))
		Begin
			update TipoPlato set descripcion=descrip 
				where codigoTipoPlato=codigo; 
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_EliminarTipoPlato(in codigo int)
		Begin
			delete from TipoPlato where codigoTipoPlato=codigo;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_BuscarTipoPlato(in codigo int)
		Begin
			select TipoPlato.codigoTipoPlato,
					TipoPlato.descripcion
						from TipoPlato
							where codigoTipoPlato=codigo;
        End$$
Delimiter ;

#Entidad: Productos

Delimiter $$
	Create procedure sp_ListarProductos()
		Begin
			select Productos.codigoProducto,
					Productos.nombreProducto,
					Productos.cantidad
						from Productos;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_AgregarProducto(in nombre varchar(150), in cantidad int)
		Begin
			insert into Productos(nombreProducto, cantidad) values(nombre, cantidad);
		End$$
Delimiter ;

Delimiter $$
	Create procedure sp_ActualizarProducto(in codigo int, in nombre varchar(150), in cant int)
		Begin
			update Productos set nombreProducto=nombre, cantidad=cant 
				where codigoProducto=codigo;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_EliminarProducto(in codigo int)
		Begin
			delete from Productos where codigoProducto=codigo;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_BuscarProducto(in codigo int)
		Begin
			select Productos.codigoProducto,
					Productos.nombreProducto,
                    Productos.cantidad
						from Productos
							where codigoProducto=codigo;
        End$$
Delimiter ;

#Entidad: Platos

Delimiter $$
	Create procedure sp_ListarPlatos()
		Begin
			select Platos.codigoPlato,
					Platos.cantidad,
                    Platos.nombrePlato,
                    Platos.descripcionPlato,
                    Platos.precioPlato,
                    Platos.codigoTipoPlato
						from Platos;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_AgregarPlato(in cant int, in nomPlato varchar(50), in descripPlato varchar(150),
										in precioPla decimal(10,2), in codTipoPlato int)
		Begin
			insert into Platos(cantidad, nombrePlato, descripcionPlato, precioPlato, codigoTipoPlato)
				values(cant, nomPlato, descripPlato, precioPla, codTipoPlato);
		End$$
Delimiter ;

Delimiter $$
	Create procedure sp_ActualizarPlato(in codigo int, in cant int, in nomPlato varchar(50), in descripPlato varchar(150),
										in precioPla decimal(10,2))
		Begin
			update Platos set cantidad=cant, nombrePlato=nomPlato, descripcionPlato=descripPlato, precioPlato=precioPla
					where codigoPlato=codigo;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_EliminarPlato(in codigo int)
		Begin
			delete from Platos where codigoPlato=codigo;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_BuscarPlato(in codigo int)
		Begin
			select Platos.codigoPlato,
					Platos.cantidad,
                    Platos.nombrePlato,
                    Platos.descripcionPlato,
                    Platos.precioPlato,
                    Platos.codigoTipoPlato
						from Platos
							where codigoPlato=codigo;
        End$$
Delimiter ;

#Entidad: Productos_has_Platos
Delimiter $$
	Create procedure sp_ListarProductos_has_Platos()
		Begin
			Select codigoPlato, codigoProducto from
				(Select Pr.codigoProducto from Productos Pr
					left join Productos_has_Platos PhP on PhP.codigoProducto = Pr.codigoProducto) c1,
				(Select Pl.codigoPlato from Platos Pl
					left join Productos_has_Platos PhP on PhP.codigoPlato = Pl.codigoPlato) c2;

        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_AgregarProducto_has_Plato(in codigoProduc int, in codigoPlat int)
		Begin
			insert into Productos_has_Platos(codigoProducto, codigoPlato) values(codigoProduc, codigoPlat);
		End$$
Delimiter ;

Delimiter $$
	Create procedure sp_ActualizarProducto_has_Plato(in codigo int, in codigoProduc int, in codigoPlat int)
		Begin
			update Productos_has_Platos set codigoProducto=codigoProduc, codigoPlato=codigoPlat
				where codigoProductos_has_Platos=codigo;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_EliminarProducto_has_Plato(in codigo int)
		Begin
			delete from Productos_has_Platos where codigoProductos_has_Platos=codigo;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_BuscarProducto_has_Plato(in codigo int)
		Begin
			select Productos_has_Platos.codigoProductos_has_Platos,
					Productos_has_Platos.codigoProducto,
                    Productos_has_Platos.codigoPlato
						from Productos_has_Platos
							where codigoProductos_has_Platos=codigo;
        End$$
Delimiter ;

#Entidad: TipoEmpleado
Delimiter $$
	Create procedure sp_ListarTipoEmpleado()
		Begin
			select TipoEmpleado.codigoTipoEmpleado,
					TipoEmpleado.descripcion
						from TipoEmpleado;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_AgregarTipoEmpleado(in descrip varchar(100))
		Begin
			insert into TipoEmpleado(descripcion) values(descrip);
		End$$
Delimiter ;

Delimiter $$
	Create procedure sp_ActualizarTipoEmpleado(in codigo int, in descrip varchar(100))
		Begin
			update TipoEmpleado set descripcion=descrip where codigoTipoEmpleado=codigo;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_EliminarTipoEmpleado(in codigo int)
		Begin
			delete from TipoEmpleado where codigoTipoEmpleado=codigo;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_BuscarTipoEmpleado(in codigo int)
		Begin
			select TipoEmpleado.codigoTipoEmpleado,
					TipoEmpleado.descripcion
						from TipoEmpleado
							where codigoTipoEmpleado=codigo;
        End$$
Delimiter ;

#Entidad: Empleados
Delimiter $$
	Create procedure sp_ListarEmpleados()
		Begin
			select Empleados.codigoEmpleado,
					Empleados.numeroEmpleado,
                    Empleados.apellidosEmpleado,
                    Empleados.nombresEmpleado,
                    Empleados.direccionEmpleado,
                    Empleados.telefonoContacto,
                    Empleados.gradoCocinero,
                    Empleados.codigoTipoEmpleado
						from Empleados;
            
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_AgregarEmpleado(in numEmple int, in apeEmple varchar(150), in nomEmple varchar(150), in direcEmple varchar(150),
											in telContact varchar(10), in gradoCoci varchar(50), in codTipoEmple int)
		Begin
			insert into Empleados(numeroEmpleado, apellidosEmpleado, nombresEmpleado, direccionEmpleado,
									telefonoContacto, gradoCocinero, codigoTipoEmpleado)
										values(numEmple, apeEmple, nomEmple, direcEmple, telContact, gradoCoci, codTipoEmple);
		End$$
Delimiter ;

Delimiter $$
	Create procedure sp_ActualizarEmpleado(in codigo int, in numEmple int, in apeEmple varchar(150), in nomEmple varchar(150), 
											in direcEmple varchar(150), in telContact varchar(10), in gradoCoci varchar(50))
		Begin
			update Empleados set numeroEmpleado=numEmple, apellidosEmpleado=apeEmple, nombresEmpleado=nomEmple,
				direccionEmpleado=direcEmple, telefonoContacto=telContact, gradoCocinero=gradoCoci
											where codigoEmpleado=codigo;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_EliminarEmpleado(in codigo int)
		Begin
			delete from Empleados where codigoEmpleado=codigo;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_BuscarEmpleado(in codigo int)
		Begin
			select Empleados.codigoEmpleado,
					Empleados.numeroEmpleado,
                    Empleados.apellidosEmpleado,
                    Empleados.nombresEmpleado,
                    Empleados.direccionEmpleado,
                    Empleados.telefonoContacto,
                    Empleados.gradoCocinero,
                    Empleados.codigoTipoEmpleado
						from Empleados
							where codigoEmpleado=codigo;
        End$$
Delimiter ;

#Entidad: Empresas
Delimiter $$
	Create procedure sp_ListarEmpresas()
		Begin
			select Empresas.codigoEmpresa,
					Empresas.nombreEmpresa,
                    Empresas.direccion,
                    Empresas.telefono
						from Empresas;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_AgregarEmpresa(in nom varchar(150), in direc varchar(150), in tel varchar(10))
		Begin
			insert into Empresas(nombreEmpresa, direccion, telefono) values(nom, direc, tel);
		End$$
Delimiter ;

Delimiter $$
	Create procedure sp_ActualizarEmpresa(in codigo int, in nom varchar(150), in direc varchar(150), in tel varchar(10))
		Begin
			update Empresas set nombreEmpresa=nom, direccion=direc, telefono=tel
				where codigoEmpresa=codigo;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_EliminarEmpresa(in codigo int)
		Begin
			delete from Empresas where codigoEmpresa=codigo;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_BuscarEmpresa(in codigo int)
		Begin
			select Empresas.codigoEmpresa,
					Empresas.nombreEmpresa,
                    Empresas.direccion,
                    Empresas.telefono
						from Empresas
							where codigoEmpresa=codigo;
        End$$
Delimiter ;

#Entidad: Presupuesto
Delimiter $$
	Create procedure sp_ListarPresupuesto()
		Begin
			select Presupuesto.codigoPresupuesto,
					Presupuesto.fechaSolicitud,
                    Presupuesto.cantidadPresupuesto,
                    Presupuesto.codigoEmpresa
						from Presupuesto;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_AgregarPresupuesto(in fechaSoli date, in cantPresu decimal(10,2), in codEmpre int)
		Begin
			insert into Presupuesto(fechaSolicitud, cantidadPresupuesto, codigoEmpresa)
				values(fechaSoli, cantPresu, codEmpre);
		End$$
Delimiter ;

Delimiter $$
	Create procedure sp_ActualizarPresupuesto(in codigo int, in fechaSoli date, in cantPresu decimal(10,2))
		Begin
			update Presupuesto set fechaSolicitud=fechaSoli, cantidadPresupuesto=cantPresu where codigoPresupuesto=codigo;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_EliminarPresupuesto(in codigo int)
		Begin
			delete from Presupuesto where codigoPresupuesto=codigo;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_BuscarPresupuesto(in codigo int)
		Begin
			select Presupuesto.codigoPresupuesto,
					Presupuesto.fechaSolicitud,
                    Presupuesto.cantidadPresupuesto,
                    Presupuesto.codigoEmpresa
						from Presupuesto
							where codigoPresupuesto=codigo;
        End$$
Delimiter ;

#Entidad: Servicios
Delimiter $$
	Create procedure sp_ListarServicios()
		Begin
			select Servicios.codigoServicio,
					Servicios.fechaServicio,
                    Servicios.tipoServicio,
                    Servicios.horaServicio,
                    Servicios.lugarServicio,
                    Servicios.telefonoContacto,
                    Servicios.codigoEmpresa
						from Servicios;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_AgregarServicio(in fechaServi date, in tipoServi varchar(100), in horaServi time, in lugarServi varchar(100),
										in telefonoServi varchar(10), in codigoEmpre int)
		Begin
			insert into Servicios(fechaServicio, tipoServicio, horaServicio, lugarServicio, telefonoContacto, codigoEmpresa)
				values(fechaServi, tipoServi, horaServi, lugarServi, telefonoServi, codigoEmpre);
		End$$
Delimiter ;

Delimiter $$
	Create procedure sp_ActualizarServicio(in codigo int, in fechaServi date, in tipoServi varchar(100), in horaServi time, 
											in lugarServi varchar(100), in telefonoContact varchar(10))
		Begin
			update Servicios set fechaServicio=fechaServi, tipoServicio=tipoServi, horaServicio=horaServi, 
				lugarServicio=lugarServi, telefonoContacto=telefonoContact
					where codigoServicio=codigo;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_EliminarServicio(in codigo int)
		Begin
			delete from Servicios where codigoServicio=codigo;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_BuscarServicio(in codigo int)
		Begin
			select Servicios.codigoServicio,
					Servicios.fechaServicio,
                    Servicios.horaServicio,
                    Servicios.tipoServicio,
                    Servicios.lugarServicio,
                    Servicios.telefonoContacto,
                    Servicios.codigoEmpresa
						from Servicios
							where codigoServicio=codigo;
        End$$
Delimiter ;

#Entidad: Servicios_has_Platos
Delimiter $$
	Create procedure sp_ListarServicios_has_Platos()
		Begin
			select codigoServicio, codigoPlato from
				(Select S.codigoServicio  as codigoServicio from Servicios S
					left join Servicios_has_Platos ShP on ShP.codigoServicio = S.codigoServicio) c1,
				(Select Pl.codigoPlato as codigoPlato from Platos Pl 
					left join Servicios_has_Platos ShP on ShP.codigoPlato = Pl.codigoPlato) c2;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_AgregarServicio_has_Plato(in codigoServi int, in codigoPlat int)
		Begin
			insert into Servicios_has_Platos(codigoServicio, codigoPlato)
				values(codigoServi, codigoPlat);
		End$$
Delimiter ;

Delimiter $$
	Create procedure sp_ActualizarServicio_has_Plato(in codigo int, in codigoServi int, in codigoPlat int)
		Begin
			update Servicios_has_Platos set codigoServicio=codigoServi, codigoPlato=codigoPlat
				where codigoServicios_has_Platos=codigo;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_EliminarServicio_has_Plato(in codigo int)
		Begin
			delete from Servicios_has_Platos where codigoServicios_has_Platos=codigo;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_BuscarServicio_has_Plato(in codigo int)
		Begin
			select Servicios_has_Platos.codigoServicios_has_Platos,
					Servicios_has_Platos.codigoServicio,
                    Servicios_has_Platos.codigoPlato
						from Servicios_has_Platos
							where codigoServicios_has_Platos=codigo;
        End$$
Delimiter ;

#Entidad: Servicios_has_Empleados

Delimiter $$
	Create procedure sp_ListarServicios_has_Empleados()
		Begin
			select Servicios_has_Empleados.codigoServicios_has_Empleados,
					Servicios_has_Empleados.codigoServicio,
                    Servicios_has_Empleados.codigoEmpleado,
                    Servicios_has_Empleados.fechaEvento,
                    Servicios_has_Empleados.horaEvento,
                    Servicios_has_Empleados.lugarEvento
						from Servicios_has_Empleados;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_AgregarServicio_has_Empleado(in codServi int, in codEmple int, in fechaEvent date, 
														in horaEvent time, in lugarEvent varchar(150))
		Begin
			insert into Servicios_has_Empleados(codigoServicio, codigoEmpleado, fechaEvento, horaEvento, lugarEvento)
				values(codServi, codEmple, fechaEvent, horaEvent, lugarEvent);
		End$$
Delimiter ;

Delimiter $$
	Create procedure sp_ActualizarServicio_has_Empleado(in codigo int, in fechaEvent date, in horaEvent time, in lugarEvent varchar(150))
		Begin
			update Servicios_has_Empleados set fechaEvento=fechaEvent, horaEvento=horaEvent, lugarEvento=lugarEvent
				where codigoServicios_has_Empleados=codigo;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_EliminarServicio_has_Empleado(in codigo int)
		Begin
			delete from Servicios_has_Empleados where codigoServicios_has_Empleados=codigo;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_BuscarServicio_has_Empleado(in codigo int)
		Begin
			select Servicios_has_Empleados.codigoServicios_has_Empleados,
					Servicios_has_Empleados.codigoServicio,
                    Servicios_has_Empleados.codigoEmpleado,
                    Servicios_has_Empleados.fechaEvento,
                    Servicios_has_Empleados.horaEvento,
                    Servicios_has_Empleados.lugarEvento
						from Servicios_has_Empleados
							where codigoServicios_has_Empleados=codigo;	
        End$$
Delimiter ;
-- -------------------------------------Procedimiento de Reportes -----------------------------------
-- ----------- Reporte Presupuesto
Delimiter $$
	Create procedure sp_ConsultaReportePresupuesto(in codEmpresa int)
    Begin
		Select * from Empresas E
			inner join Presupuesto P on E.codigoEmpresa = P.codigoEmpresa
				where E.codigoEmpresa = codEmpresa group by P.cantidadPresupuesto;
    End$$
Delimiter ;

Delimiter $$
	Create procedure sp_ReportePresupuesto(in codEmpresa int)
    Begin
		Select * from Empresas E
			inner join Servicios S on E.codigoEmpresa = S.codigoEmpresa 
				where E.codigoEmpresa = codEmpresa;
    End$$
Delimiter ;

-- ----------------------- Reporte Servicios con subreportes --------------------------
-- Listar codigos Platos
Delimiter $$
	Create procedure sp_SubReporteListarCodPlatos(in codServicio int)
    Begin
		Select codigoPlato from
			(Select S.codigoServicio as codigoServicio from Servicios S
				left join Servicios_has_Platos ShP on S.codigoServicio = ShP.codigoServicio) c1,
			(Select Pl.codigoPlato as codigoPlato from Platos Pl
				left join Servicios_has_Platos ShP on ShP.codigoPlato = Pl.codigoPlato) c2
				where codigoServicio = codServicio;			
    End$$
Delimiter ;

-- Descripcion tipo plato
Delimiter $$
	Create procedure sp_SubReporteDescripcionTP(in codPlato int)
    Begin
		Select TP.descripcion from TipoPlato TP
			inner join Platos Pl on Pl.codigoTipoPlato = TP.codigoTipoPlato
				where Pl.codigoPlato = codPlato;
	End$$
Delimiter ;

-- Listar codigo Producto
Delimiter $$
	Create procedure sp_SubReporteListarCodProducto(in codPlato int)
    Begin
		Select codigoProducto from 
			(Select Pr.codigoProducto as codigoProducto from Productos Pr 
				left join Productos_has_Platos PhP on PhP.codigoProducto = Pr.codigoProducto) c1,
			(Select Pl.codigoPlato as codigoPlato from Platos Pl
				left join Productos_has_Platos PhP on PhP.codigoPlato = Pl.codigoPlato) c2
				where codigoPlato = codPlato;
    End$$
Delimiter ;

-- Nombre Producto
Delimiter $$
	Create procedure sp_SubReporteNombreProducto(in codProducto int)
    Begin
		Select Pr.nombreProducto from Productos Pr
			where codigoProducto = codProducto;
    End$$
Delimiter ;

-- Cantidad platos
Delimiter $$
	Create procedure sp_ListarCantidadPlatos(in codServicio int)
    Begin
		Select count(codigoPlato) from
			(Select S.codigoServicio  as codigoServicio from Servicios S
				left join Servicios_has_Platos ShP on ShP.codigoServicio = S.codigoServicio) c1,
			(Select Pl.codigoPlato as codigoPlato from Platos Pl
				left Join Servicios_has_Platos ShP on ShP.codigoPlato = Pl.codigoPlato) c2
					where codigoServicio =  codServicio;
    End$$
Delimiter ;