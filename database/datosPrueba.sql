-- CATEGORIAS
INSERT INTO `categorias` (`idCategoria`, `Nombre`, `foto`) VALUES
(1, 'Familiar', ''),
(2, 'concierto', '40cb06d8-5451-4a06-9956-3f8157921fab.jpg'),
(3, 'Deportivo', '4a472824-ca28-49a7-887c-a68c611ae24a.jpg'),
(4, 'Privado', '543e9c15-1fea-49d0-ab15-f6c5b6862e50.jpg'),
(5, 'Cultural', '05bfe24f-037e-44f9-9952-7e49e5ec7f4a.jpg'),
(6, 'Gastronomia', 'd16ff8cd-048f-46b5-aa9d-1778d7130605.jpg'),
(7, 'Educativo', '0e67af25-c5ae-4334-8f98-75854aca07a1.jpg'),
(8, 'Empresarial', NULL),
(9, 'Musical', NULL),
(10, 'Religioso', NULL),
(11, 'Tecnología', NULL),
(12, 'Arte y Exposiciones', NULL);

-- ESTADOS
INSERT INTO `estados` (`idEstado`, `nombre`, `descripcion`) VALUES
(1, 'registro activo', 'registro activo'),
(2, 'registro inactivo', 'registro inactivo'),
(3, 'registro pendiente', 'registro pendiente'),
(4, 'Evento activo', 'Evento activo'),
(5, 'Evento inactivo', 'Evento inactivo'),
(6, 'Evento pendiente', 'Evento pendiente');

-- ROLES
INSERT INTO `roles` (`idEstado`, `idRoles`, `descripcion`, `nombre`) VALUES
(1, 1, 'se encargara de crear y publicar sus eventos', 'organizador'),
(1, 2, 'encargado de la gestion del sistema', 'administrador'),
(1, 3, 'usuario encargado de comprar', 'cliente');

-- USUARIOS
INSERT INTO `usuario` (`idEstado`, `idRoles`, `idUsuario`, `apellido`, `clave`, `correo`, `nombre`, `telefono`) VALUES
(1, 2, 1, 'Tordecilla Faria', 'prueba123', '[eduardestf20@gmail.com](mailto:eduardestf20@gmail.com)', 'Eduard Santiago', '3046139087'),
(1, 1, 2, 'Torres Narvaez', 'holacarolina', '[angiecarolinatorresnarvaez@gmail.com](mailto:angiecarolinatorresnarvaez@gmail.com)', 'Angie Carolina', '3000005555'),
(1, 3, 3, 'Saez Agamez', 'memo', '[contacteljhonny@gmail.com](mailto:contacteljhonny@gmail.com)', 'Jhonatan', '3029991144'),
(1, 3, 4, 'Bermudez Mora', 'holabermudez', '[alebermudezmora@gmail.com](mailto:alebermudezmora@gmail.com)', 'Alejandra Sofia', '3008972233'),
(1, 1, 5, 'Martinez Lopez', 'carlos2025', '[carlosmartinez@gmail.com](mailto:carlosmartinez@gmail.com)', 'Carlos Alberto', '3015678901'),
(1, 1, 6, 'Rodriguez Perez', 'maria123', '[mariarodriguez@hotmail.com](mailto:mariarodriguez@hotmail.com)', 'Maria Fernanda', '3102345678'),
(1, 1, 7, 'Garcia Silva', 'juanorg', '[juangarcia@outlook.com](mailto:juangarcia@outlook.com)', 'Juan Carlos', '3209876543'),
(1, 1, 8, 'Mendez Castro', 'luisa456', '[luisamendez@gmail.com](mailto:luisamendez@gmail.com)', 'Luisa Maria', '3186543210'),
(1, 3, 11, 'Saez Agamez', 'ale123', '[bermudez@gmail.com](mailto:bermudez@gmail.com)', 'Alejandra Mora', '323876973'),
(1, 3, 13, 'Saez Agamez', 'juanca@', '[juacamusic@gmail.com](mailto:juacamusic@gmail.com)', 'Juan camilo', '3229098778'),
(1, 3, 14, 'ambuila cardenaz', 'jcambuila34', '[jcambuila@hotmail.com](mailto:jcambuila@hotmail.com)', 'Juan camilo', '3129092345'),
(1, 3, 16, 'monterroza romero', 'ricardo@romero', '[adrianricardo@gmal.com](mailto:adrianricardo@gmal.com)', 'adrian ricardo', '3210008990');

-- EVENTOS
INSERT INTO `evento` (`fecha`, `hora`, `idCategoria`, `idEstado`, `idEvento`, `idOrganizador`, `titulo`, `foto`, `lugar`, `descripcion`) VALUES
('2025-11-30', '12:30:00.000000', 6, 4, 1, 2, 'Festival gastronomico caribeño', '', 'Centro de Convenciones', 'Sabores auténticos del Caribe en un festival culinario inolvidable. ¡No te lo pierdas!'),
('2025-11-15', '17:00:00.000000', 5, 4, 2, 2, 'Festival nautico de cartagena', '', 'Bahia de cartagena', 'Un festival nautico para todo publico'),
('2025-11-23', '20:00:00.000000', 2, 4, 3, 2, 'Concierto Andres cepeda', '', 'centro de convenciones', 'andres cepada hace una nueva llegada a cartagena, ¡ven y disfruta!'),
('2025-11-20', '15:00:00.000000', 1, 4, 4, 5, 'Día de la Familia en el Parque', NULL, 'Parque Simón Bolívar', 'Actividades recreativas, juegos inflables y show infantil para toda la familia'),
('2025-12-08', '10:00:00.000000', 1, 4, 5, 6, 'Festival Infantil de Navidad', NULL, 'Club Cartagena', 'Celebración navideña con show de magia, payasos y regalos para los niños'),
('2025-12-22', '16:00:00.000000', 1, 4, 6, 5, 'Picnic Familiar de Fin de Año', NULL, 'Parque del Centenario', 'Tarde de juegos, comida y diversión para compartir en familia'),
('2025-11-28', '19:00:00.000000', 2, 4, 7, 7, 'Noche de Karaoke', NULL, 'Bar El Encuentro', 'Ven a demostrar tu talento cantando tus canciones favoritas'),
('2025-12-15', '20:30:00.000000', 2, 6, 8, 8, 'Fiesta Temática Años 80', NULL, 'Salón de Eventos Plaza Mayor', 'Revive la época dorada con música, vestuario y decoración retro'),
('2025-11-17', '08:00:00.000000', 3, 4, 9, 9, 'Maratón de Cartagena 2025', NULL, 'Centro Histórico de Cartagena', 'Carrera atlética de 10K y 21K recorriendo las calles históricas'),
('2025-12-01', '16:00:00.000000', 3, 4, 10, 5, 'Torneo de Fútbol Playa', NULL, 'Playa de Bocagrande', 'Competencia de fútbol playa con equipos locales y nacionales'),
('2025-12-20', '07:00:00.000000', 3, 4, 11, 6, 'Copa de Natación Caribeña', NULL, 'Complejo Acuático Municipal', 'Campeonato regional de natación con diferentes categorías'),
('2025-11-25', '18:00:00.000000', 4, 6, 12, 7, 'Celebración Aniversario Corporativo', NULL, 'Hotel Hilton Cartagena', 'Evento exclusivo para celebrar 25 años de la empresa'),
('2025-12-10', '19:30:00.000000', 4, 6, 13, 8, 'Boda en el Caribe', NULL, 'Casa Pestagua', 'Ceremonia y recepción de boda en lugar histórico'),
('2025-12-28', '20:00:00.000000', 4, 6, 14, 9, 'Cena de Gala Privada', NULL, 'Restaurante 1621', 'Cena exclusiva de fin de año para grupo empresarial'),
('2025-11-22', '17:00:00.000000', 5, 4, 15, 2, 'Festival de Teatro Contemporáneo', NULL, 'Teatro Adolfo Mejía', 'Muestra de obras teatrales de grupos locales e internacionales'),
('2025-12-05', '19:00:00.000000', 5, 4, 16, 5, 'Encuentro de Danza Folclórica', NULL, 'Plaza de la Aduana', 'Presentación de danzas tradicionales colombianas y caribeñas'),
('2025-12-18', '18:00:00.000000', 5, 4, 17, 6, 'Ciclo de Cine Latinoamericano', NULL, 'Cinemateca Distrital', 'Proyección de películas premiadas del cine latinoamericano'),
('2025-12-03', '18:00:00.000000', 6, 4, 18, 7, 'Cata de Vinos y Quesos', NULL, 'Hotel Santa Clara', 'Degustación exclusiva de vinos importados y quesos gourmet'),
('2025-12-12', '11:00:00.000000', 6, 4, 19, 8, 'Festival del Ceviche', NULL, 'Muelle de los Pegasos', 'Competencia y degustación de diferentes estilos de ceviche'),
('2025-12-27', '19:00:00.000000', 6, 4, 20, 2, 'Feria de Comida Internacional', NULL, 'Plaza de Bolívar', 'Variedad gastronómica de diferentes países del mundo'),
('2025-11-19', '14:00:00.000000', 7, 4, 21, 9, 'Taller de Fotografía Digital', NULL, 'Centro Cultural', 'Aprende técnicas profesionales de fotografía con expertos'),
('2025-11-27', '09:00:00.000000', 7, 4, 22, 5, 'Seminario de Emprendimiento', NULL, 'Cámara de Comercio', 'Conferencias sobre cómo iniciar y gestionar tu propio negocio'),
('2025-12-14', '15:00:00.000000', 7, 4, 23, 6, 'Curso de Cocina Internacional', NULL, 'Escuela Gastronómica Del Caribe', 'Aprende a preparar platos de diferentes culturas del mundo'),
('2025-11-21', '08:30:00.000000', 8, 4, 24, 7, 'Congreso de Negocios del Caribe', NULL, 'Centro de Convenciones', 'Encuentro empresarial para networking y desarrollo de negocios'),
('2025-12-06', '09:00:00.000000', 8, 4, 25, 8, 'Feria Comercial y Exportaciones', NULL, 'Centro Internacional de Negocios', 'Exposición de productos y servicios para exportación'),
('2025-12-19', '14:00:00.000000', 8, 4, 26, 9, 'Workshop de Liderazgo Empresarial', NULL, 'Hotel Las Americas', 'Taller intensivo sobre gestión y liderazgo para ejecutivos'),
('2025-11-16', '19:00:00.000000', 9, 4, 27, 2, 'Festival de Rock Latino', NULL, 'Parque Simón Bolívar', 'Concierto con las mejores bandas de rock en español'),
('2025-11-29', '20:00:00.000000', 9, 4, 28, 5, 'Noche de Salsa y Bachata', NULL, 'Plaza de Toros', 'Gran fiesta con orquestas en vivo de salsa y bachata'),
('2025-12-31', '22:00:00.000000', 9, 4, 29, 6, 'Concierto de Fin de Año', NULL, 'Castillo de San Felipe', 'Despide el año con música variada y fuegos artificiales'),
('2025-12-24', '18:00:00.000000', 10, 4, 30, 7, 'Misa de Navidad', NULL, 'Catedral de Cartagena', 'Celebración eucarística de la Nochebuena'),
('2025-11-18', '10:00:00.000000', 11, 4, 31, 8, 'Hackathon Cartagena Tech', NULL, 'Universidad Tecnológica de Bolívar', 'Maratón de programación de 24 horas para desarrolladores'),
('2025-12-04', '15:00:00.000000', 11, 4, 32, 9, 'Conferencia de Inteligencia Artificial', NULL, 'Centro de Innovación Digital', 'Charlas sobre IA y sus aplicaciones en diferentes industrias'),
('2025-12-17', '09:00:00.000000', 11, 4, 33, 5, 'Expo Tecnología y Videojuegos', NULL, 'Mall Plaza El Castillo', 'Exhibición de últimas tecnologías y torneos de videojuegos'),
('2025-11-24', '17:00:00.000000', 12, 4, 34, 6, 'Exposición de Arte Contemporáneo', NULL, 'Museo de Arte Moderno', 'Muestra de artistas colombianos emergentes'),
('2025-12-07', '18:00:00.000000', 12, 4, 35, 7, 'Galería de Fotografía Urbana', NULL, 'Casa del Marqués de Valdehoyos', 'Exposición fotográfica sobre la vida en Cartagena'),
('2025-12-21', '16:00:00.000000', 12, 4, 36, 8, 'Muestra de Escultura y Pintura', NULL, 'Centro de Formación de la Cooperación Española', 'Exhibición de obras de artistas locales e internacionales');


-- LOCALIDADES
INSERT INTO `localidades` (`capacidad`, `disponibles`, `precio`, `idEvento`, `idLocalidades`, `nombre`) VALUES
... (todos los INSERT correspondientes) ...

-- EVENTOS DESEADOS
INSERT INTO `eventodeseados` (`idEvento`, `idEventoDeseado`, `idUsuario`) VALUES
(1, 1, 4),
(2, 2, 3),
(1, 3, 3);

-- VALORACIÓN
INSERT INTO `valoracion` (`calificacion`, `idCliente`, `idEvento`, `idValoracion`, `comentario`) VALUES
(5, 4, 39, 1, 'excelente concierto, la organizacion supo hacerlo y sin contratiempos'),
(5, 4, 1, 3, 'me gusto mucho, la organizacion se merece mas
eventos en la ciudad asi como este'),
(4, 4, 2, 4, 'la ciudad deberia estar mas organizada al momento de eventos como este. hay demasiadas filas de personas esperando por embarcaciones y genera desorden en la ciudad');

-- COMPRA
INSERT INTO `compra` (`idCompra`, `total`, `fechaCompra`, `idCliente`, `metodoPago`) VALUES
(1, 45000.00, '2025-11-22 21:52:57.000000', 3, 'Tarjeta de Crédito'),
(2, 180000.00, '2025-11-22 22:01:20.000000', 3, 'Tarjeta de Crédito'),
(3, 180000.00, '2025-11-22 22:08:43.000000', 16, 'Tarjeta de Crédito'),
(4, 180000.00, '2025-11-22 22:14:14.000000', 16, 'Tarjeta de Crédito'),
(5, 200000.00, '2025-11-22 22:15:28.000000', 16, 'Tarjeta de Crédito'),
(6, 360000.00, '2025-11-22 22:18:14.000000', 3, 'Tarjeta de Crédito');

-- TIQUETE
INSERT INTO `tiquete` (`idTiquete`, `idLocalidades`, `codigoQR`) VALUES
(1, 58, '11be2d70-f202-49a0-a84f-60da9fe8df69'),
(2, 58, 'd8588421-d077-4228-bf45-ae8b88ddbf8a'),
(3, 58, '5d5e4a03-63ba-42f5-9ef6-78eba55e0666'),
(4, 58, '1d334cdf-83d0-47f4-be37-ab89576e1aa1'),
(5, 58, 'a0af8c5c-3f9f-4b5d-b481-05ee893f7f69'),
(6, 58, 'ecb88a3e-8ad5-4b93-a9d3-310a949472dc'),
(7, 58, '558c8b26-e270-4186-b65e-44fe80a9ecc2'),
(8, 58, '1ec53fb2-680a-4385-a713-c36ef8bb7dad'),
(9, 58, '62b68dd8-6fcd-4304-9895-91e21069ac0d'),
(10, 42, '8c99f67a-e799-4e50-95b0-4a43559fd883'),
(11, 5, 'e1b43e33-af37-4d9b-a74e-a113c8210cdb'),
(12, 5, '068fc668-40e6-45f5-b3a3-18e752e05625'),
(13, 5, '78f2a57b-956c-48d7-9650-81bde98235bc'),
(14, 5, '2ce73359-a87d-4f86-a335-a7ceb14562c6'),
(15, 42, 'e24ea3a0-6b26-49c0-a018-4376acf43a85'),
(16, 42, '64d1cfa3-b60a-4041-9f75-978d24118a3c');

-- TIQUETE_COMPRA
INSERT INTO `tiquete_compra` (`cantidad`, `idCompra`, `idTiquete`, `idTiquete_Compra`) VALUES
(1, 1, 1, 1),
(1, 2, 2, 2),
(1, 2, 3, 3),
(1, 2, 4, 4),
(1, 2, 5, 5),
(1, 3, 6, 6),
(1, 3, 7, 7),
(1, 3, 8, 8),
(1, 3, 9, 9),
(1, 4, 10, 10),
(1, 5, 11, 11),
(1, 5, 12, 12),
(1, 5, 13, 13),
(1, 5, 14, 14),
(1, 6, 15, 15),
(1, 6, 16, 16);
