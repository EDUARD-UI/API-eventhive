-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 23-11-2025 a las 20:43:07
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `eventhive`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `categorias`
--

CREATE TABLE `categorias` (
  `idCategoria` bigint(20) NOT NULL,
  `Nombre` varchar(45) NOT NULL,
  `foto` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `categorias`
--

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

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `compra`
--

CREATE TABLE `compra` (
  `idCompra` int(11) NOT NULL,
  `total` decimal(10,2) DEFAULT NULL,
  `fechaCompra` datetime(6) DEFAULT NULL,
  `idCliente` bigint(20) NOT NULL,
  `metodoPago` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `compra`
--

INSERT INTO `compra` (`idCompra`, `total`, `fechaCompra`, `idCliente`, `metodoPago`) VALUES
(1, 45000.00, '2025-11-22 21:52:57.000000', 3, 'Tarjeta de Crédito'),
(2, 180000.00, '2025-11-22 22:01:20.000000', 3, 'Tarjeta de Crédito'),
(3, 180000.00, '2025-11-22 22:08:43.000000', 16, 'Tarjeta de Crédito'),
(4, 180000.00, '2025-11-22 22:14:14.000000', 16, 'Tarjeta de Crédito'),
(5, 200000.00, '2025-11-22 22:15:28.000000', 16, 'Tarjeta de Crédito'),
(6, 360000.00, '2025-11-22 22:18:14.000000', 3, 'Tarjeta de Crédito');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `estados`
--

CREATE TABLE `estados` (
  `idEstado` bigint(20) NOT NULL,
  `nombre` varchar(45) DEFAULT NULL,
  `descripcion` varchar(60) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `estados`
--

INSERT INTO `estados` (`idEstado`, `nombre`, `descripcion`) VALUES
(1, 'registro activo', 'registro activo'),
(2, 'registro inactivo', 'registro inactivo'),
(3, 'registro pendiente', 'registro pendiente'),
(4, 'Evento activo', 'Evento activo'),
(5, 'Evento inactivo', 'Evento inactivo'),
(6, 'Evento pendiente', 'Evento pendiente');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `evento`
--

CREATE TABLE `evento` (
  `fecha` date DEFAULT NULL,
  `hora` time(6) DEFAULT NULL,
  `idCategoria` bigint(20) NOT NULL,
  `idEstado` bigint(20) NOT NULL,
  `idEvento` bigint(20) NOT NULL,
  `idOrganizador` bigint(20) NOT NULL,
  `titulo` varchar(45) NOT NULL,
  `foto` varchar(100) DEFAULT NULL,
  `lugar` varchar(100) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `evento`
--

INSERT INTO `evento` (`fecha`, `hora`, `idCategoria`, `idEstado`, `idEvento`, `idOrganizador`, `titulo`, `foto`, `lugar`, `descripcion`) VALUES
('2025-11-30', '12:30:00.000000', 6, 4, 1, 2, 'Festival gastronomico caribeño', 'e5fc65e3-a0de-485d-ab0f-058ad9b5d941.jpg', 'Centro de Convenciones', 'Sabores auténticos del Caribe en un festival culinario inolvidable. ¡No te lo pierdas!'),
('2025-11-15', '17:00:00.000000', 5, 4, 2, 2, 'Festival nautico de cartagena', 'b33e5ca5-ed2f-45fb-a8e2-c1a4605fcb54.jpg', 'Bahia de cartagena', 'Un festival nautico para todo publico'),
('2025-11-23', '20:00:00.000000', 2, 4, 3, 2, 'Concierto Andres cepeda', 'd51332f9-57f0-49d1-a27a-0a338804eacb.jpg', 'centro de convenciones', 'andres cepada hace una nueva llegada a cartagena, ¡ven y disfruta!'),
('2025-11-20', '15:00:00.000000', 1, 4, 4, 5, 'Día de la Familia en el Parque', NULL, 'Parque Simón Bolívar', 'Actividades recreativas, juegos inflables y show infantil para toda la familia'),
('2025-12-08', '10:00:00.000000', 1, 4, 5, 6, 'Festival Infantil de Navidad', NULL, 'Club Cartagena', 'Celebración navideña con show de magia, payasos y regalos para los niños'),
('2025-12-22', '16:00:00.000000', 1, 4, 6, 5, 'Picnic Familiar de Fin de Año', NULL, 'Parque del Centenario', 'Tarde de juegos, comida y diversión para compartir en familia'),
('2025-11-28', '19:00:00.000000', 2, 4, 7, 7, 'Noche de Karaoke', NULL, 'Bar El Encuentro', 'Ven a demostrar tu talento cantando tus canciones favoritas'),
('2025-12-15', '20:30:00.000000', 2, 6, 8, 8, 'Fiesta Temática Años 80', NULL, 'Salón de Eventos Plaza Mayor', 'Revive la época dorada con música, vestuario y decoración retro'),
('2025-12-01', '16:00:00.000000', 3, 4, 10, 5, 'Torneo de Fútbol Playa', NULL, 'Playa de Bocagrande', 'Competencia de fútbol playa con equipos locales y nacionales'),
('2025-12-20', '07:00:00.000000', 3, 4, 11, 6, 'Copa de Natación Caribeña', NULL, 'Complejo Acuático Municipal', 'Campeonato regional de natación con diferentes categorías'),
('2025-11-25', '18:00:00.000000', 4, 6, 12, 7, 'Celebración Aniversario Corporativo', NULL, 'Hotel Hilton Cartagena', 'Evento exclusivo para celebrar 25 años de la empresa'),
('2025-12-10', '19:30:00.000000', 4, 6, 13, 8, 'Boda en el Caribe', NULL, 'Casa Pestagua', 'Ceremonia y recepción de boda en lugar histórico'),
('2025-11-22', '17:00:00.000000', 5, 4, 15, 2, 'Festival de Teatro Contemporáneo', NULL, 'Teatro Adolfo Mejía', 'Muestra de obras teatrales de grupos locales e internacionales'),
('2025-12-05', '19:00:00.000000', 5, 4, 16, 5, 'Encuentro de Danza Folclórica', NULL, 'Plaza de la Aduana', 'Presentación de danzas tradicionales colombianas y caribeñas'),
('2025-12-18', '18:00:00.000000', 5, 4, 17, 6, 'Ciclo de Cine Latinoamericano', NULL, 'Cinemateca Distrital', 'Proyección de películas premiadas del cine latinoamericano'),
('2025-12-03', '18:00:00.000000', 6, 4, 18, 7, 'Cata de Vinos y Quesos', NULL, 'Hotel Santa Clara', 'Degustación exclusiva de vinos importados y quesos gourmet'),
('2025-12-12', '11:00:00.000000', 6, 4, 19, 8, 'Festival del Ceviche', NULL, 'Muelle de los Pegasos', 'Competencia y degustación de diferentes estilos de ceviche'),
('2025-12-27', '19:00:00.000000', 6, 4, 20, 2, 'Feria de Comida Internacional', NULL, 'Plaza de Bolívar', 'Variedad gastronómica de diferentes países del mundo'),
('2025-11-27', '09:00:00.000000', 7, 4, 22, 5, 'Seminario de Emprendimiento', NULL, 'Cámara de Comercio', 'Conferencias sobre cómo iniciar y gestionar tu propio negocio'),
('2025-12-14', '15:00:00.000000', 7, 4, 23, 6, 'Curso de Cocina Internacional', NULL, 'Escuela Gastronómica Del Caribe', 'Aprende a preparar platos de diferentes culturas del mundo'),
('2025-11-21', '08:30:00.000000', 8, 4, 24, 7, 'Congreso de Negocios del Caribe', NULL, 'Centro de Convenciones', 'Encuentro empresarial para networking y desarrollo de negocios'),
('2025-12-06', '09:00:00.000000', 8, 4, 25, 8, 'Feria Comercial y Exportaciones', NULL, 'Centro Internacional de Negocios', 'Exposición de productos y servicios para exportación'),
('2025-11-16', '19:00:00.000000', 9, 4, 27, 2, 'Festival de Rock Latino', '0936354c-9369-479c-9abe-e7c53d8bbc16.jpg', 'Parque Simón Bolívar', 'Concierto con las mejores bandas de rock en español'),
('2025-11-29', '20:00:00.000000', 9, 4, 28, 5, 'Noche de Salsa y Bachata', NULL, 'Plaza de Toros', 'Gran fiesta con orquestas en vivo de salsa y bachata'),
('2025-12-31', '22:00:00.000000', 9, 4, 29, 6, 'Concierto de Fin de Año', NULL, 'Castillo de San Felipe', 'Despide el año con música variada y fuegos artificiales'),
('2025-12-24', '18:00:00.000000', 10, 4, 30, 7, 'Misa de Navidad', NULL, 'Catedral de Cartagena', 'Celebración eucarística de la Nochebuena'),
('2025-11-18', '10:00:00.000000', 11, 4, 31, 8, 'Hackathon Cartagena Tech', NULL, 'Universidad Tecnológica de Bolívar', 'Maratón de programación de 24 horas para desarrolladores'),
('2025-12-17', '09:00:00.000000', 11, 4, 33, 5, 'Expo Tecnología y Videojuegos', NULL, 'Mall Plaza El Castillo', 'Exhibición de últimas tecnologías y torneos de videojuegos'),
('2025-11-24', '17:00:00.000000', 12, 4, 34, 6, 'Exposición de Arte Contemporáneo', NULL, 'Museo de Arte Moderno', 'Muestra de artistas colombianos emergentes'),
('2025-12-07', '18:00:00.000000', 12, 4, 35, 7, 'Galería de Fotografía Urbana', NULL, 'Casa del Marqués de Valdehoyos', 'Exposición fotográfica sobre la vida en Cartagena'),
('2025-12-21', '16:00:00.000000', 12, 4, 36, 8, 'Muestra de Escultura y Pintura', NULL, 'Centro de Formación de la Cooperación Española', 'Exhibición de obras de artistas locales e internacionales'),
('2025-12-02', '17:00:00.000000', 2, 4, 39, 2, 'Concierto de hamilton', '470c1f4b-2cfa-4e45-8823-24141ed33921.jpg', 'estadio jaime moron', 'hamilton regresa de la USA y finaliza su gira afrorockstar en su amada cartagena'),
('2025-11-27', '14:27:00.000000', 5, 4, 53, 2, 'festival de danza folclorica', '4f2a11ec-5956-4981-bd93-2c0de4a912d7.jpg', 'Centro de Convenciones', 'disfruta de la cultura del folclor de la region caribe'),
('2025-11-14', '17:30:00.000000', 6, 4, 54, 2, 'festival gastronimo de comida guatemalteca', '2e4cddaf-1bcd-4517-a242-693123dca407.jpg', 'Centro de Convenciones', 'la comida guatemalteca se toma la ciudad de cartagena, ven y disfruta de la cultura y gastronomia extrejera'),
('2025-04-01', '10:00:00.000000', 1, 4, 220, 2, 'Festival de Primavera Familiar', NULL, 'Parque de Bolívar', 'Celebración de la primavera con actividades para niños'),
('2025-04-08', '15:00:00.000000', 1, 4, 221, 5, 'Taller de Jardinería Infantil', NULL, 'Jardín Botánico', 'Aprende sobre plantas y naturaleza'),
('2025-04-15', '11:00:00.000000', 1, 4, 222, 6, 'Show de Payasos y Malabaristas', NULL, 'Plaza de la Paz', 'Diversión y risas garantizadas'),
('2025-04-22', '16:00:00.000000', 1, 4, 223, 7, 'Feria de Ciencias para Niños', NULL, 'Colegio San Lucas', 'Experimentación científica divertida'),
('2025-04-29', '14:00:00.000000', 1, 4, 224, 8, 'Olimpiadas Matemáticas', NULL, 'Universidad de Cartagena', 'Competencia de matemáticas para jóvenes'),
('2025-04-05', '20:00:00.000000', 2, 4, 225, 2, 'Concierto de Rock Nacional', NULL, 'Coliseo Cubierto', 'Las mejores bandas de rock colombiano'),
('2025-04-12', '21:00:00.000000', 2, 4, 226, 5, 'Noche de Salsa Casino', NULL, 'Club Caribe', 'Aprende a bailar salsa casino'),
('2025-04-19', '19:00:00.000000', 2, 4, 227, 6, 'Festival de Música Folclórica', NULL, 'Plaza de los Coches', 'Música tradicional colombiana'),
('2025-04-26', '20:30:00.000000', 2, 4, 228, 7, 'Concierto de Jazz Fusion', NULL, 'Teatro Heredia', 'Fusión de jazz con ritmos caribeños'),
('2025-05-03', '22:00:00.000000', 2, 4, 229, 8, 'Fiesta de Música Urbana', NULL, 'Discoteca La Terraza', 'Reggaeton, trap y hip hop'),
('2025-04-02', '08:00:00.000000', 3, 4, 230, 2, 'Maratón Nocturno', NULL, 'Centro Histórico', 'Carrera nocturna por el centro amurallado'),
('2025-04-09', '07:00:00.000000', 3, 4, 231, 5, 'Torneo de Baloncesto 3x3', NULL, 'Canchas de San Diego', 'Competencia de baloncesto callejero'),
('2025-04-16', '16:00:00.000000', 3, 4, 232, 6, 'Campeonato de Tenis de Mesa', NULL, 'Coliseo de Combate', 'Torneo de ping pong para todas las edades'),
('2025-04-23', '09:00:00.000000', 3, 4, 233, 7, 'Competencia de Ciclismo de Montaña', NULL, 'Cerro de La Popa', 'Recorrido de montaña para ciclistas'),
('2025-04-30', '14:00:00.000000', 3, 4, 234, 8, 'Torneo de Voleibol Playa', NULL, 'Playa de Bocagrande', 'Competencia de voleibol en la arena'),
('2025-04-06', '18:00:00.000000', 5, 4, 235, 2, 'Exposición de Arte Naif', NULL, 'Galería de Arte Popular', 'Arte ingenuo y espontáneo'),
('2025-04-13', '19:00:00.000000', 5, 4, 236, 5, 'Noche de Teatro Experimental', NULL, 'Teatro Miramar', 'Obras vanguardistas y contemporáneas'),
('2025-04-20', '17:00:00.000000', 5, 4, 237, 6, 'Festival de Danzas Africanas', NULL, 'Plaza de la Trinidad', 'Ritmos y danzas del continente africano'),
('2025-04-27', '20:00:00.000000', 5, 4, 238, 7, 'Concierto de Música Barroca', NULL, 'Iglesia Santo Domingo', 'Música del período barroco europeo'),
('2025-05-04', '16:00:00.000000', 5, 4, 239, 8, 'Muestra de Cine Independiente', NULL, 'Cinemateca Distrital', 'Películas de realizadores independientes'),
('2025-04-07', '12:00:00.000000', 6, 4, 240, 2, 'Festival de Comida Mexicana', NULL, 'Plaza México', 'Sabores picantes y auténticos'),
('2025-04-14', '19:00:00.000000', 6, 4, 241, 5, 'Cena Maridaje Vinos', NULL, 'Bodega de Vinos', 'Armonía entre comida y vino'),
('2025-04-21', '11:00:00.000000', 6, 4, 242, 6, 'Mercado Orgánico', NULL, 'Parque del Centenario', 'Productos naturales y ecológicos'),
('2025-04-28', '18:00:00.000000', 6, 4, 243, 7, 'Festival del Café', NULL, 'Casa del Café', 'Catación y preparación de café'),
('2025-05-05', '14:00:00.000000', 6, 4, 244, 8, 'Taller de Coctelería', NULL, 'Bar La Cevichería', 'Aprende a preparar cócteles'),
('2025-04-03', '09:00:00.000000', 7, 4, 245, 2, 'Taller de Oratoria', NULL, 'Centro de Convenciones', 'Mejora tus habilidades para hablar en público'),
('2025-04-10', '15:00:00.000000', 7, 4, 246, 5, 'Seminario de Fotografía', NULL, 'Escuela de Arte', 'Técnicas avanzadas de fotografía'),
('2025-04-17', '10:00:00.000000', 7, 4, 247, 6, 'Curso de Primeros Auxilios', NULL, 'Cruz Roja', 'Aprende a salvar vidas'),
('2025-04-24', '18:00:00.000000', 7, 4, 248, 7, 'Charla de Emprendimiento', NULL, 'Incubadora de Negocios', 'Cómo iniciar tu propio negocio'),
('2025-05-01', '14:00:00.000000', 7, 4, 249, 8, 'Taller de Meditación', NULL, 'Centro de Yoga', 'Técnicas de relajación y mindfulness'),
('2025-04-04', '08:30:00.000000', 8, 4, 250, 2, 'Congreso de Marketing Digital', NULL, 'Hotel Estelar', 'Estrategias digitales para empresas'),
('2025-04-11', '09:00:00.000000', 8, 4, 251, 5, 'Feria de Franquicias', NULL, 'Centro de Exposiciones', 'Oportunidades de negocio'),
('2025-04-18', '14:00:00.000000', 8, 4, 252, 6, 'Workshop de Ventas', NULL, 'Cámara de Comercio', 'Técnicas de venta efectivas'),
('2025-04-25', '10:00:00.000000', 8, 4, 253, 7, 'Conferencia de Logística', NULL, 'Universidad Tecnológica', 'Optimización de cadena de suministro'),
('2025-05-02', '16:00:00.000000', 8, 4, 254, 8, 'Networking Industrial', NULL, 'Club de Ejecutivos', 'Conexiones del sector industrial'),
('2025-05-06', '20:00:00.000000', 9, 4, 255, 2, 'Festival de Música Caribeña', NULL, 'Playa de Marbella', 'Ritmos tradicionales del Caribe'),
('2025-05-13', '21:00:00.000000', 9, 4, 256, 5, 'Noche de Música Romántica', NULL, 'Hotel del Mar', 'Canciones de amor para parejas'),
('2025-05-20', '19:00:00.000000', 9, 4, 257, 6, 'Concierto de Música Celta', NULL, 'Parque del Periodista', 'Sonidos mágicos de Irlanda y Escocia'),
('2025-05-27', '20:30:00.000000', 9, 4, 258, 7, 'Fiesta de Música Disco', NULL, 'Discoteca Studio 54', 'Revive los años 70'),
('2025-06-03', '22:00:00.000000', 9, 4, 259, 8, 'Festival de Reggae', NULL, 'Playa de la Boquilla', 'Vibraciones jamaiquinas'),
('2025-05-07', '18:00:00.000000', 10, 4, 260, 2, 'Concierto de Alabanza', NULL, 'Iglesia San Roque', 'Música cristiana contemporánea'),
('2025-05-14', '10:00:00.000000', 10, 4, 261, 5, 'Peregrinación Mariana', NULL, 'Cerro de La Popa', 'Camino de fe y devoción'),
('2025-05-21', '19:00:00.000000', 10, 4, 262, 6, 'Velada Eucarística', NULL, 'Catedral Metropolitana', 'Adoración al santísimo'),
('2025-05-28', '17:00:00.000000', 10, 4, 263, 7, 'Festival de Coros Gospel', NULL, 'Teatro Adolfo Mejía', 'Música espiritual en coro'),
('2025-06-04', '16:00:00.000000', 10, 4, 264, 8, 'Celebración Pentecostés', NULL, 'Parroquia San Pedro', 'Fiesta del espíritu santo'),
('2025-05-08', '09:00:00.000000', 11, 4, 265, 2, 'Hackathon Blockchain', NULL, 'Centro de Innovación', 'Desarrollo de aplicaciones blockchain'),
('2025-05-15', '15:00:00.000000', 11, 4, 266, 5, 'Conferencia de Ciberseguridad', NULL, 'Universidad de Cartagena', 'Protección digital y hacking ético'),
('2025-05-22', '11:00:00.000000', 11, 4, 267, 6, 'Expo Drones y Robótica', NULL, 'Coliseo Cubierto', 'Tecnología aérea y automatización'),
('2025-05-29', '18:00:00.000000', 11, 4, 268, 7, 'Taller de Realidad Virtual', NULL, 'Centro Tecnológico', 'Inmersión en mundos virtuales'),
('2025-06-05', '14:00:00.000000', 11, 4, 269, 8, 'Feria de Videojuegos', NULL, 'Mall Plaza', 'Torneos y novedades gaming'),
('2025-05-09', '17:00:00.000000', 12, 4, 270, 2, 'Exposición de Arte Digital', NULL, 'Galería Digital', 'Arte creado con tecnología'),
('2025-05-16', '18:00:00.000000', 12, 4, 271, 5, 'Muestra de Fotografía Documental', NULL, 'Museo Histórico', 'Fotografía que cuenta historias'),
('2025-05-23', '16:00:00.000000', 12, 4, 272, 6, 'Festival de Arte Performance', NULL, 'Plaza de Santo Domingo', 'Arte en vivo y espontáneo'),
('2025-05-30', '19:00:00.000000', 12, 4, 273, 7, 'Exposición de Escultura Moderna', NULL, 'Jardines del Club', 'Obras tridimensionales contemporáneas'),
('2025-06-06', '15:00:00.000000', 12, 4, 274, 8, 'Feria de Artesanías', NULL, 'Bóvedas de Santa Clara', 'Arte y manualidades locales'),
('2025-06-07', '10:00:00.000000', 1, 4, 275, 2, 'Día del Niño Especial', NULL, 'Parque de la Marina', 'Celebración inclusiva para todos los niños'),
('2025-06-14', '15:00:00.000000', 1, 4, 276, 5, 'Taller de Reciclaje Creativo', NULL, 'Centro Ambiental', 'Transforma desechos en arte'),
('2025-06-21', '11:00:00.000000', 1, 4, 277, 6, 'Fiesta de la Espuma', NULL, 'Parque del Centenario', 'Diversión con espuma y música'),
('2025-06-28', '16:00:00.000000', 1, 4, 278, 7, 'Show de Títeres Gigantes', NULL, 'Plaza de la Paz', 'Títeres de gran formato'),
('2025-07-05', '14:00:00.000000', 1, 4, 279, 8, 'Olimpiadas Acuáticas', NULL, 'Piscina Municipal', 'Competencias en el agua'),
('2025-06-08', '20:00:00.000000', 2, 4, 280, 2, 'Concierto de Música Andina', NULL, 'Plaza de la Aduana', 'Sonidos de los Andes colombianos'),
('2025-06-15', '21:00:00.000000', 2, 4, 281, 5, 'Noche de Blues y Soul', NULL, 'Pub The Blues', 'Música con sentimiento y pasión'),
('2025-06-22', '19:00:00.000000', 2, 4, 282, 6, 'Festival de Música Indie', NULL, 'Parque Fernández Madrid', 'Bandas independientes emergentes'),
('2025-06-29', '20:30:00.000000', 2, 4, 283, 7, 'Concierto de Música Clásica', NULL, 'Teatro Heredia', 'Obras maestras de la música clásica'),
('2025-07-06', '22:00:00.000000', 2, 4, 284, 8, 'Fiesta de Música Electrónica', NULL, 'Playa de Bocagrande', 'DJs nacionales e internacionales'),
('2025-06-09', '08:00:00.000000', 3, 4, 285, 2, 'Triatlón Cartagena', NULL, 'Playas de Castillogrande', 'Natación, ciclismo y carrera'),
('2025-06-16', '07:00:00.000000', 3, 4, 286, 5, 'Torneo de Fútbol Sala', NULL, 'Coliseo de Combate', 'Competencia de fútbol rápido'),
('2025-06-23', '16:00:00.000000', 3, 4, 287, 6, 'Campeonato de Ajedrez Rápido', NULL, 'Casa del Ajedrez', 'Partidas de ajedrez contra reloj'),
('2025-06-30', '09:00:00.000000', 3, 4, 288, 7, 'Competencia de Atletismo', NULL, 'Estadio Jaime Morón', 'Pruebas de pista y campo'),
('2025-07-07', '14:00:00.000000', 3, 4, 289, 8, 'Torneo de Voleibol Playa', NULL, 'Playa de Marbella', 'Competencia en arena'),
('2025-07-12', '10:00:00.000000', 1, 4, 290, 2, 'Festival de Verano Familiar', NULL, 'Parque de Bolívar', 'Actividades acuáticas y juegos de verano'),
('2025-07-19', '15:00:00.000000', 1, 4, 291, 5, 'Taller de Cocina para Niños', NULL, 'Escuela de Cocina Kids', 'Aprende recetas divertidas y saludables'),
('2025-07-26', '11:00:00.000000', 1, 4, 292, 6, 'Show de Magia con Animales', NULL, 'Centro Comercial Caribe Plaza', 'Magia interactiva con mascotas adiestradas'),
('2025-08-02', '16:00:00.000000', 1, 4, 293, 7, 'Feria de Ciencias Divertidas', NULL, 'Colegio San Pedro Claver', 'Experimentos científicos para toda la familia'),
('2025-08-09', '14:00:00.000000', 1, 4, 294, 8, 'Olimpiadas de Juegos Tradicionales', NULL, 'Parque del Centenario', 'Competencias de trompo, yo-yo y canicas'),
('2025-08-16', '17:00:00.000000', 1, 4, 295, 2, 'Noche de Películas Familiares', NULL, 'Cine al Aire Libre', 'Proyección de películas clásicas familiares'),
('2025-08-23', '10:00:00.000000', 1, 4, 296, 5, 'Taller de Huerta Casera', NULL, 'Jardín Botánico', 'Aprende a cultivar tus propias verduras'),
('2025-08-30', '15:00:00.000000', 1, 4, 297, 6, 'Festival de Globos de Agua', NULL, 'Parque Apolo', 'Batalla de globos y juegos acuáticos'),
('2025-09-06', '11:00:00.000000', 1, 4, 298, 7, 'Show de Títeres con Luz Negra', NULL, 'Teatro Miramar', 'Espectáculo de títeres fluorescentes'),
('2025-09-13', '16:00:00.000000', 1, 4, 299, 8, 'Feria de Robótica Infantil', NULL, 'Centro de Innovación', 'Robots y tecnología para niños'),
('2025-07-13', '20:00:00.000000', 2, 4, 300, 2, 'Concierto de Rock Sinfónico', NULL, 'Teatro Adolfo Mejía', 'Rock clásico con orquesta sinfónica'),
('2025-07-20', '21:00:00.000000', 2, 4, 301, 5, 'Noche de Salsa Brava', NULL, 'Casa de la Salsa', 'Salsa tradicional con orquestas en vivo'),
('2025-07-27', '19:00:00.000000', 2, 4, 302, 6, 'Festival de Música Llanera', NULL, 'Plaza de Toros', 'Joropo y música llanera colombiana'),
('2025-08-03', '20:30:00.000000', 2, 4, 303, 7, 'Concierto de Música New Age', NULL, 'Jardín Zen', 'Música para relajación y meditación'),
('2025-08-10', '22:00:00.000000', 2, 4, 304, 8, 'Fiesta de Música Dance', NULL, 'Discoteca La Movida', 'Los mejores hits dance internacionales'),
('2025-08-17', '19:00:00.000000', 2, 4, 305, 2, 'Concierto de Música Barroca', NULL, 'Iglesia San Pedro Claver', 'Obras del período barroco europeo'),
('2025-08-24', '20:00:00.000000', 2, 4, 306, 5, 'Noche de Jazz Latino', NULL, 'Bar Jazz Club', 'Fusión de jazz con ritmos latinos'),
('2025-08-31', '21:00:00.000000', 2, 4, 307, 6, 'Festival de Música Country', NULL, 'Salón Rodeo', 'Line dance y música country americana'),
('2025-09-07', '19:30:00.000000', 9, 4, 308, 7, 'Concierto de Música Minimalista', NULL, 'Auditorio Augusto covo, Tecnologico Comfenalco', 'Música contemporánea experimental'),
('2025-09-14', '20:30:00.000000', 2, 4, 309, 8, 'Noche de Música Lounge', NULL, 'Sky Bar', 'Ambient music con vista al mar'),
('2025-07-14', '08:00:00.000000', 3, 4, 310, 2, 'Maratón de la Ciudad Amurallada', NULL, 'Centro Histórico', 'Recorrido por las murallas de Cartagena'),
('2025-07-21', '07:00:00.000000', 3, 4, 311, 5, 'Torneo de Pádel Amateur', NULL, 'Club de Pádel', 'Competencia de pádel para todas las edades'),
('2025-07-28', '16:00:00.000000', 3, 4, 312, 6, 'Campeonato de Ajedrez Relámpago', NULL, 'Casa del Ajedrez', 'Partidas rápidas de ajedrez'),
('2025-08-04', '09:00:00.000000', 3, 4, 313, 7, 'Competencia de Natación en Aguas Abiertas', NULL, 'Bahía de Cartagena', 'Natación en mar abierto'),
('2025-08-11', '14:00:00.000000', 3, 4, 314, 8, 'Torneo de Voleibol Playa Mixto', NULL, 'Playa de Castillogrande', 'Competencia mixta de voleibol playa'),
('2025-08-18', '08:00:00.000000', 3, 4, 315, 2, 'Carrera de Obstáculos Extremos', NULL, 'Fuerte de San Fernando', 'Circuito de obstáculos desafiantes'),
('2025-08-25', '07:00:00.000000', 3, 4, 316, 5, 'Torneo de Baloncesto 3x3 Femenino', NULL, 'Canchas de San Diego', 'Competencia femenina de baloncesto callejero'),
('2025-09-01', '16:00:00.000000', 3, 4, 317, 6, 'Campeonato de Tenis de Mesa Dobles', NULL, 'Coliseo de Combate', 'Competencia por parejas de ping pong'),
('2025-09-08', '09:00:00.000000', 3, 4, 318, 7, 'Competencia de Ciclismo de Ruta', NULL, 'Vía a Turbaco', 'Recorrido de carretera para ciclistas'),
('2025-09-15', '14:00:00.000000', 3, 4, 319, 8, 'Torneo de Fútbol Playa Juvenil', NULL, 'Playa de Bocagrande', 'Competencia para jóvenes futbolistas'),
('2025-07-15', '18:00:00.000000', 5, 4, 320, 2, 'Exposición de Arte Contemporáneo Africano', NULL, 'Museo de Arte Moderno', 'Arte moderno del continente africano'),
('2025-07-22', '19:00:00.000000', 5, 4, 321, 5, 'Noche de Teatro Absurdo', NULL, 'Teatro Heredia', 'Obras del teatro del absurdo'),
('2025-07-29', '17:00:00.000000', 5, 4, 322, 6, 'Festival de Danzas Asiáticas', NULL, 'Plaza de la Aduana', 'Danzas tradicionales de Asia'),
('2025-08-05', '20:00:00.000000', 5, 4, 323, 7, 'Concierto de Música Renacentista', NULL, 'Iglesia Santo Domingo', 'Música del período renacentista'),
('2025-08-12', '16:00:00.000000', 5, 4, 324, 8, 'Muestra de Cine Documental', NULL, 'Cinemateca Distrital', 'Documentales de realizadores locales'),
('2025-08-19', '18:00:00.000000', 5, 4, 325, 2, 'Exposición de Fotografía de Naturaleza', NULL, 'Galería Natural', 'Fotografía de flora y fauna'),
('2025-08-26', '19:00:00.000000', 5, 4, 326, 5, 'Noche de Teatro Clásico Griego', NULL, 'Teatro al Aire Libre', 'Obras de la antigua Grecia'),
('2025-09-02', '17:00:00.000000', 5, 4, 327, 6, 'Festival de Danzas Europeas', NULL, 'Plaza de los Coches', 'Danzas tradicionales europeas'),
('2025-09-09', '20:00:00.000000', 5, 4, 328, 7, 'Concierto de Música Medieval', NULL, 'Castillo de San Felipe', 'Música de la edad media'),
('2025-09-16', '16:00:00.000000', 5, 4, 329, 8, 'Muestra de Cine de Animación', NULL, 'Cinemateca Distrital', 'Cortometrajes de animación'),
('2025-07-16', '12:00:00.000000', 6, 4, 330, 2, 'Festival de Comida Italiana', NULL, 'Plaza Italia', 'Sabores auténticos de Italia'),
('2025-07-23', '19:00:00.000000', 6, 4, 331, 5, 'Cena de Alta Cocina Francesa', NULL, 'Restaurante Francés', 'Menú degustación francés'),
('2025-07-30', '11:00:00.000000', 6, 4, 332, 6, 'Mercado de Productos Locales', NULL, 'Parque del Centenario', 'Productos frescos de la región'),
('2025-08-06', '18:00:00.000000', 6, 4, 333, 7, 'Festival del Ceviche Peruano', NULL, 'Muelle de los Pegasos', 'Diferentes estilos de ceviche peruano'),
('2025-08-13', '14:00:00.000000', 6, 4, 334, 8, 'Taller de Panadería Artesanal', NULL, 'Panadería Tradicional', 'Aprende a hacer pan artesanal'),
('2025-08-20', '12:00:00.000000', 6, 4, 335, 2, 'Festival de Comida Tailandesa', NULL, 'Centro de Convenciones', 'Sabores picantes y exóticos de Tailandia'),
('2025-08-27', '19:00:00.000000', 6, 4, 336, 5, 'Cena Maridaje Cervezas Artesanales', NULL, 'Cervecería Artesanal', 'Armonía entre comida y cerveza'),
('2025-09-03', '11:00:00.000000', 6, 4, 337, 6, 'Mercado de Especias y Condimentos', NULL, 'Plaza de Mercado', 'Especias de todo el mundo'),
('2025-09-10', '18:00:00.000000', 6, 4, 338, 7, 'Festival de la Arepa Colombiana', NULL, 'Plaza de la Trinidad', 'Todos los tipos de arepas colombianas'),
('2025-09-17', '14:00:00.000000', 6, 4, 339, 8, 'Taller de Repostería Francesa', NULL, 'Pastelería Francesa', 'Aprende postres franceses clásicos'),
('2025-07-17', '09:00:00.000000', 7, 4, 340, 2, 'Taller de Programación para Niños', NULL, 'Centro de Computación', 'Introducción a la programación infantil'),
('2025-07-24', '15:00:00.000000', 7, 4, 341, 5, 'Seminario de Inteligencia Emocional', NULL, 'Centro de Desarrollo Personal', 'Manejo de emociones y relaciones'),
('2025-07-31', '10:00:00.000000', 7, 4, 342, 6, 'Curso de Inglés Conversacional', NULL, 'Instituto de Idiomas', 'Práctica de conversación en inglés'),
('2025-08-07', '18:00:00.000000', 7, 4, 343, 7, 'Taller de Finanzas para Jóvenes', NULL, 'Biblioteca Distrital', 'Educación financiera temprana'),
('2025-08-14', '14:00:00.000000', 7, 4, 344, 8, 'Charla de Nutrición Deportiva', NULL, 'Centro Deportivo', 'Alimentación para atletas'),
('2025-08-21', '09:00:00.000000', 7, 4, 345, 2, 'Taller de Robótica Educativa', NULL, 'Centro de Innovación', 'Robótica para principiantes'),
('2025-08-28', '15:00:00.000000', 7, 4, 346, 5, 'Seminario de Comunicación Asertiva', NULL, 'Centro de Convenciones', 'Comunicación efectiva y respetuosa'),
('2025-09-04', '10:00:00.000000', 7, 4, 347, 6, 'Curso de Francés Básico', NULL, 'Alianza Francesa', 'Introducción al idioma francés'),
('2025-09-11', '18:00:00.000000', 7, 4, 348, 7, 'Taller de Manejo del Estrés', NULL, 'Centro de Bienestar', 'Técnicas anti estrés'),
('2025-09-18', '14:00:00.000000', 7, 4, 349, 8, 'Charla de Educación Sexual Integral', NULL, 'Colegio Mayor', 'Educación sexual para adolescentes'),
('2025-07-18', '08:30:00.000000', 8, 4, 350, 2, 'Congreso de Transformación Digital', NULL, 'Centro de Convenciones', 'Digitalización de procesos empresariales'),
('2025-07-25', '09:00:00.000000', 8, 4, 351, 5, 'Feria de Proveedores Locales', NULL, 'Plaza Mayor', 'Conectando empresas con proveedores'),
('2025-08-01', '14:00:00.000000', 8, 4, 352, 6, 'Workshop de Innovación Empresarial', NULL, 'Hotel Hilton', 'Metodologías ágiles y creativas'),
('2025-08-08', '10:00:00.000000', 8, 4, 353, 7, 'Conferencia de Sostenibilidad Corporativa', NULL, 'Universidad Tecnológica', 'Responsabilidad social empresarial'),
('2025-08-15', '16:00:00.000000', 8, 4, 354, 8, 'Networking Sector Turístico', NULL, 'Hotel Caribe', 'Conexiones en la industria turística'),
('2025-08-22', '08:30:00.000000', 8, 4, 355, 2, 'Congreso de Comercio Exterior', NULL, 'Centro de Convenciones', 'Importación y exportación'),
('2025-08-29', '09:00:00.000000', 8, 4, 356, 5, 'Feria de Servicios Empresariales', NULL, 'Centro de Exposiciones', 'Servicios para empresas'),
('2025-09-05', '14:00:00.000000', 8, 4, 357, 6, 'Workshop de Gestión de Proyectos', NULL, 'Cámara de Comercio', 'Metodologías PMI y ágiles'),
('2025-09-12', '10:00:00.000000', 8, 4, 358, 7, 'Conferencia de Economía Naranja', NULL, 'Universidad de Cartagena', 'Industrias creativas y culturales'),
('2025-09-19', '16:00:00.000000', 8, 4, 359, 8, 'Networking Sector Salud', NULL, 'Club Médico', 'Conexiones en el sector salud'),
('2025-07-19', '20:00:00.000000', 9, 4, 360, 2, 'Festival de Música Andina Colombiana', NULL, 'Plaza de la Aduana', 'Música tradicional de los Andes'),
('2025-07-26', '21:00:00.000000', 9, 4, 361, 5, 'Noche de Salsa Casino', NULL, 'Club Caribe', 'Estilo casino cubano'),
('2025-08-02', '19:00:00.000000', 9, 4, 362, 6, 'Concierto de Música Clásica Romántica', NULL, 'Teatro Heredia', 'Obras del período romántico'),
('2025-08-09', '20:30:00.000000', 9, 4, 363, 7, 'Festival de Música Electrónica Ambient', NULL, 'Jardines del Club', 'Electrónica ambiental y chillout'),
('2025-08-16', '22:00:00.000000', 9, 4, 364, 8, 'Fiesta de Música Urbana Latino', NULL, 'Discoteca La Terraza', 'Reggaeton y trap latino'),
('2025-08-23', '19:00:00.000000', 9, 4, 365, 2, 'Concierto de Música Barroca Francesa', NULL, 'Iglesia San Pedro Claver', 'Música barroca de Francia'),
('2025-08-30', '20:00:00.000000', 9, 4, 366, 5, 'Noche de Jazz Fusión', NULL, 'Bar Jazz Club', 'Fusión de jazz con otros géneros'),
('2025-09-06', '21:00:00.000000', 9, 4, 367, 6, 'Festival de Música Country Line Dance', NULL, 'Salón Rodeo', 'Baile country en línea'),
('2025-09-13', '19:30:00.000000', 9, 4, 368, 7, 'Concierto de Música Minimalista Americana', NULL, 'Auditorio Universidad', 'Minimalismo estadounidense'),
('2025-09-20', '20:30:00.000000', 9, 4, 369, 8, 'Noche de Música Lounge Chill', NULL, 'Sky Bar', 'Lounge relajante'),
('2025-07-20', '18:00:00.000000', 10, 4, 370, 2, 'Concierto de Alabanza Contemporánea', NULL, 'Iglesia San Roque', 'Música cristiana moderna'),
('2025-07-27', '10:00:00.000000', 10, 4, 371, 5, 'Retiro Espiritual Juvenil', NULL, 'Monasterio La Popa', 'Jornada de reflexión para jóvenes'),
('2025-08-03', '19:00:00.000000', 10, 4, 372, 6, 'Velada Eucarística Nocturna', NULL, 'Catedral Metropolitana', 'Adoración nocturna al santísimo'),
('2025-08-10', '17:00:00.000000', 10, 4, 373, 7, 'Festival de Coros Parroquiales', NULL, 'Basílica Menor', 'Coros de diferentes parroquias'),
('2025-08-17', '16:00:00.000000', 10, 4, 374, 8, 'Celebración de la Asunción', NULL, 'Santuario María Auxiliadora', 'Fiesta mariana de la asunción'),
('2025-08-24', '18:00:00.000000', 10, 4, 375, 2, 'Concierto de Música Sacra', NULL, 'Iglesia Santo Toribio', 'Música religiosa clásica'),
('2025-08-31', '10:00:00.000000', 10, 4, 376, 5, 'Peregrinación Familiar', NULL, 'Cerro de La Popa', 'Camino de fe en familia'),
('2025-09-07', '19:00:00.000000', 10, 4, 377, 6, 'Velada Mariana Especial', NULL, 'Santuario María Auxiliadora', 'Celebración mariana especial'),
('2025-09-14', '17:00:00.000000', 10, 4, 378, 7, 'Festival de Coros Gospel', NULL, 'Teatro Adolfo Mejía', 'Coros gospel espirituales'),
('2025-09-21', '16:00:00.000000', 10, 4, 379, 8, 'Celebración del Perdón', NULL, 'Parroquia San Pedro', 'Sacramento de la reconciliación'),
('2025-07-21', '09:00:00.000000', 11, 4, 380, 2, 'Hackathon de Realidad Aumentada', NULL, 'Centro de Innovación', 'Desarrollo de aplicaciones AR'),
('2025-07-28', '15:00:00.000000', 11, 4, 381, 5, 'Conferencia de Machine Learning', NULL, 'Universidad de Cartagena', 'Aprendizaje automático e IA'),
('2025-08-04', '11:00:00.000000', 11, 4, 382, 6, 'Expo Drones y Fotografía Aérea', NULL, 'Coliseo Cubierto', 'Fotografía y video con drones'),
('2025-08-11', '18:00:00.000000', 11, 4, 383, 7, 'Taller de Realidad Virtual Inmersiva', NULL, 'Centro Tecnológico', 'Experiencias VR inmersivas'),
('2025-08-18', '14:00:00.000000', 11, 4, 384, 8, 'Feria de Videojuegos Indie', NULL, 'Mall Plaza', 'Videojuegos independientes'),
('2025-08-25', '09:00:00.000000', 11, 4, 385, 2, 'Hackathon de Blockchain', NULL, 'Centro de Innovación', 'Aplicaciones descentralizadas'),
('2025-09-01', '15:00:00.000000', 11, 4, 386, 5, 'Conferencia de Ciberseguridad Avanzada', NULL, 'Universidad Tecnológica', 'Seguridad informática avanzada'),
('2025-09-08', '11:00:00.000000', 11, 4, 387, 6, 'Expo Robótica Industrial', NULL, 'Centro de Convenciones', 'Robótica para industria'),
('2025-09-15', '18:00:00.000000', 11, 4, 388, 7, 'Taller de IoT Doméstico', NULL, 'Centro Tecnológico', 'Internet de las cosas en hogares'),
('2025-09-22', '14:00:00.000000', 11, 4, 389, 8, 'Feria de Startups Tecnológicas', NULL, 'Incubadora de Empresas', 'Emprendimiento tecnológico'),
('2025-07-22', '17:00:00.000000', 12, 4, 390, 2, 'Exposición de Arte Digital Interactivo', NULL, 'Galería Digital', 'Arte digital con interacción'),
('2025-07-29', '18:00:00.000000', 12, 4, 391, 5, 'Muestra de Fotografía Conceptual', NULL, 'Museo de Arte Moderno', 'Fotografía artística conceptual'),
('2025-08-05', '16:00:00.000000', 12, 4, 392, 6, 'Festival de Arte Callejero Urbano', NULL, 'Getsemaní', 'Arte urbano en vivo'),
('2025-08-12', '19:00:00.000000', 12, 4, 393, 7, 'Exposición de Escultura Contemporánea', NULL, 'Jardines del Club', 'Esculturas modernas'),
('2025-08-19', '15:00:00.000000', 12, 4, 394, 8, 'Feria de Artesanías Locales', NULL, 'Bóvedas de Santa Clara', 'Artesanías de la región'),
('2025-08-26', '17:00:00.000000', 12, 4, 395, 2, 'Exposición de Pintura Abstracta', NULL, 'Galería de Arte Moderno', 'Arte abstracto contemporáneo'),
('2025-09-02', '18:00:00.000000', 12, 4, 396, 5, 'Muestra de Fotografía de Retrato', NULL, 'Casa del Marqués', 'Retratos artísticos'),
('2025-09-09', '16:00:00.000000', 12, 4, 397, 6, 'Festival de Performance Art', NULL, 'Plaza de Santo Domingo', 'Arte en vivo performático'),
('2025-09-16', '19:00:00.000000', 12, 4, 398, 7, 'Exposición de Arte Naif Internacional', NULL, 'Museo de Arte', 'Arte ingenuo mundial'),
('2025-09-23', '15:00:00.000000', 12, 4, 399, 8, 'Feria de Cerámica Artesanal', NULL, 'Centro Artesanal', 'Cerámica y alfarería'),
('2025-09-30', '10:00:00.000000', 1, 4, 400, 2, 'Festival de Otoño Familiar', NULL, 'Parque de Bolívar', 'Actividades temáticas de otoño para toda la familia'),
('2025-10-07', '15:00:00.000000', 1, 4, 401, 5, 'Taller de Halloween para Niños', NULL, 'Centro Comercial Caribe Plaza', 'Preparación segura para Halloween'),
('2025-10-14', '11:00:00.000000', 1, 4, 402, 6, 'Show de Magia de Halloween', NULL, 'Teatro Miramar', 'Magia y misterio para la temporada'),
('2025-10-21', '16:00:00.000000', 1, 4, 403, 7, 'Feria de Ciencias de Halloween', NULL, 'Colegio San Lucas', 'Experimentos científicos espeluznantes'),
('2025-10-28', '14:00:00.000000', 1, 4, 404, 8, 'Concurso de Disfraces Familiar', NULL, 'Plaza de la Paz', 'Competencia de disfraces en familia'),
('2025-11-04', '17:00:00.000000', 1, 4, 405, 2, 'Noche de Juegos de Mesa', NULL, 'Centro Comercial La Serrezuela', 'Juegos de mesa para todas las edades'),
('2025-11-11', '10:00:00.000000', 1, 4, 406, 5, 'Taller de Manualidades Navideñas', NULL, 'Casa de la Cultura', 'Preparando la navidad con manualidades'),
('2025-11-18', '15:00:00.000000', 1, 4, 407, 6, 'Festival de Villancicos Infantiles', NULL, 'Plaza de Santo Domingo', 'Niños cantando villancicos'),
('2025-11-25', '11:00:00.000000', 1, 4, 408, 7, 'Show de Títeres Navideños', NULL, 'Teatro Heredia', 'Títeres con temática navideña'),
('2025-12-02', '16:00:00.000000', 1, 4, 409, 8, 'Feria de Juguetes Educativos', NULL, 'Centro de Convenciones', 'Juguetes que enseñan y divierten'),
('2025-09-28', '20:00:00.000000', 2, 4, 410, 2, 'Concierto de Rock Progresivo', NULL, 'Teatro Adolfo Mejía', 'Rock progresivo y experimental'),
('2025-10-05', '21:00:00.000000', 2, 4, 411, 5, 'Noche de Salsa Romántica', NULL, 'Casa de la Salsa', 'Salsa para enamorados'),
('2025-10-12', '19:00:00.000000', 2, 4, 412, 6, 'Festival de Música del Pacífico', NULL, 'Plaza de la Aduana', 'Música tradicional del Pacífico colombiano'),
('2025-10-19', '20:30:00.000000', 2, 4, 413, 7, 'Concierto de Música Ambient', NULL, 'Jardín Botánico', 'Música ambiental y relajante'),
('2025-10-26', '22:00:00.000000', 2, 4, 414, 8, 'Fiesta de Música House', NULL, 'Discoteca La Movida', 'House music toda la noche'),
('2025-11-02', '19:00:00.000000', 2, 4, 415, 2, 'Concierto de Música Clásica Rusa', NULL, 'Iglesia San Pedro Claver', 'Obras de compositores rusos'),
('2025-11-09', '20:00:00.000000', 2, 4, 416, 5, 'Noche de Jazz Contemporáneo', NULL, 'Bar Jazz Club', 'Jazz moderno y vanguardista'),
('2025-11-16', '21:00:00.000000', 2, 4, 417, 6, 'Festival de Música Tradicional China', NULL, 'Centro Cultural', 'Música milenaria china'),
('2025-11-23', '19:30:00.000000', 2, 4, 418, 7, 'Concierto de Música Experimental', NULL, 'Auditorio Universidad', 'Sonidos experimentales y vanguardistas'),
('2025-11-30', '20:30:00.000000', 2, 4, 419, 8, 'Noche de Música Chillout', NULL, 'Sky Bar', 'Música relajante para desconectar'),
('2025-09-29', '08:00:00.000000', 3, 4, 420, 2, 'Maratón Nocturno Familiar', NULL, 'Avenida San Martín', 'Carrera nocturna para toda la familia'),
('2025-10-06', '07:00:00.000000', 3, 4, 421, 5, 'Torneo de Pádel Dobles Mixto', NULL, 'Club de Pádel', 'Competencia mixta de pádel'),
('2025-10-13', '16:00:00.000000', 3, 4, 422, 6, 'Campeonato de Ajedrez por Equipos', NULL, 'Casa del Ajedrez', 'Competencia grupal de ajedrez'),
('2025-10-20', '09:00:00.000000', 3, 4, 423, 7, 'Competencia de Natación Master', NULL, 'Piscina Olímpica', 'Pruebas para nadadores veteranos'),
('2025-10-27', '14:00:00.000000', 3, 4, 424, 8, 'Torneo de Fútbol Playa Femenino', NULL, 'Playa de Castillogrande', 'Competencia femenina de fútbol playa'),
('2025-11-03', '08:00:00.000000', 3, 4, 425, 2, 'Triatlón Familiar', NULL, 'Playas de Marbella', 'Triatlón adaptado para familias'),
('2025-11-10', '07:00:00.000000', 3, 4, 426, 5, 'Torneo de Baloncesto 3x3 Juvenil', NULL, 'Canchas de San Diego', 'Competencia para jóvenes'),
('2025-11-17', '16:00:00.000000', 3, 4, 427, 6, 'Campeonato de Tenis de Mesa Master', NULL, 'Coliseo de Combate', 'Competencia para mayores de 40 años'),
('2025-11-24', '09:00:00.000000', 3, 4, 428, 7, 'Competencia de Ciclismo Urbano', NULL, 'Centro Histórico', 'Recorrido urbano en bicicleta'),
('2025-12-01', '14:00:00.000000', 3, 4, 429, 8, 'Torneo de Voleibol Playa Juvenil', NULL, 'Playa de Bocagrande', 'Competencia para jóvenes'),
('2025-09-30', '18:00:00.000000', 5, 4, 430, 2, 'Exposición de Arte Surrealista', NULL, 'Museo de Arte Moderno', 'Arte surrealista y onírico'),
('2025-10-07', '19:00:00.000000', 5, 4, 431, 5, 'Noche de Teatro de Comedia', NULL, 'Teatro Heredia', 'Comedia y humor en escena'),
('2025-10-14', '17:00:00.000000', 5, 4, 432, 6, 'Festival de Danzas Caribeñas', NULL, 'Plaza de los Coches', 'Danzas tradicionales del Caribe'),
('2025-10-21', '20:00:00.000000', 5, 4, 433, 7, 'Concierto de Música Clásica Alemana', NULL, 'Iglesia Santo Domingo', 'Obras de compositores alemanes'),
('2025-10-28', '16:00:00.000000', 5, 4, 434, 8, 'Muestra de Cine de Terror', NULL, 'Cinemateca Distrital', 'Películas de terror y suspenso'),
('2025-11-04', '18:00:00.000000', 5, 4, 435, 2, 'Exposición de Fotografía Urbana', NULL, 'Galería Urbana', 'Fotografía de la vida en la ciudad'),
('2025-11-11', '19:00:00.000000', 5, 4, 436, 5, 'Noche de Teatro Dramático', NULL, 'Teatro al Aire Libre', 'Drama y emociones intensas'),
('2025-11-18', '17:00:00.000000', 5, 4, 437, 6, 'Festival de Danzas Contemporáneas', NULL, 'Plaza de la Trinidad', 'Danza moderna y contemporánea'),
('2025-11-25', '20:00:00.000000', 5, 4, 438, 7, 'Concierto de Música Tradicional Japonesa', NULL, 'Centro Cultural', 'Música tradicional del Japón'),
('2025-12-02', '16:00:00.000000', 5, 4, 439, 8, 'Muestra de Cine Navideño', NULL, 'Cinemateca Distrital', 'Películas con temática navideña'),
('2025-10-01', '12:00:00.000000', 6, 4, 440, 2, 'Festival de Comida Española', NULL, 'Plaza España', 'Sabores auténticos de España'),
('2025-10-08', '19:00:00.000000', 6, 4, 441, 5, 'Cena de Cocina Mediterránea', NULL, 'Restaurante Mediterráneo', 'Menú degustación mediterráneo'),
('2025-10-15', '11:00:00.000000', 6, 4, 442, 6, 'Mercado de Productos Orgánicos', NULL, 'Parque del Centenario', 'Productos ecológicos y naturales'),
('2025-10-22', '18:00:00.000000', 6, 4, 443, 7, 'Festival de la Hamburguesa Gourmet', NULL, 'Plaza de Bolívar', 'Hamburguesas gourmet creativas'),
('2025-10-29', '14:00:00.000000', 6, 4, 444, 8, 'Taller de Coctelería Tropical', NULL, 'Bar Tropical', 'Cocteles con frutas tropicales'),
('2025-11-05', '12:00:00.000000', 6, 4, 445, 2, 'Festival de Comida Argentina', NULL, 'Restaurante Argentino', 'Carnes y sabores argentinos'),
('2025-11-12', '19:00:00.000000', 6, 4, 446, 5, 'Cena de Cocina Peruana', NULL, 'Restaurante Peruano', 'Menú degustación peruano'),
('2025-11-19', '11:00:00.000000', 6, 4, 447, 6, 'Mercado de Panes Artesanales', NULL, 'Plaza de los Panes', 'Panes de diferentes regiones'),
('2025-11-26', '18:00:00.000000', 6, 4, 448, 7, 'Festival de la Pizza Artesanal', NULL, 'Pizzería Tradicional', 'Pizzas artesanales creativas'),
('2025-12-03', '14:00:00.000000', 6, 4, 449, 8, 'Taller de Repostería Navideña', NULL, 'Pastelería Navideña', 'Postres típicos de navidad'),
('2025-10-02', '09:00:00.000000', 7, 4, 450, 2, 'Taller de Programación Web', NULL, 'Centro de Computación', 'Desarrollo web para principiantes'),
('2025-10-09', '15:00:00.000000', 7, 4, 451, 5, 'Seminario de Marketing Digital Avanzado', NULL, 'Centro de Convenciones', 'Estrategias digitales avanzadas'),
('2025-10-16', '10:00:00.000000', 7, 4, 452, 6, 'Curso de Italiano Básico', NULL, 'Instituto de Idiomas', 'Introducción al idioma italiano'),
('2025-10-23', '18:00:00.000000', 7, 4, 453, 7, 'Taller de Finanzas Personales', NULL, 'Biblioteca Distrital', 'Manejo inteligente del dinero'),
('2025-10-30', '14:00:00.000000', 7, 4, 454, 8, 'Charla de Nutrición Holística', NULL, 'Centro de Salud', 'Alimentación integral y saludable'),
('2025-11-06', '09:00:00.000000', 7, 4, 455, 2, 'Taller de Fotografía de Producto', NULL, 'Escuela de Fotografía', 'Fotografía comercial y publicitaria'),
('2025-11-13', '15:00:00.000000', 7, 4, 456, 5, 'Seminario de Liderazgo Transformacional', NULL, 'Universidad de Cartagena', 'Liderazgo que transforma'),
('2025-11-20', '10:00:00.000000', 7, 4, 457, 6, 'Curso de Chino Mandarín Básico', NULL, 'Instituto de Idiomas', 'Introducción al mandarín'),
('2025-11-27', '18:00:00.000000', 7, 4, 458, 7, 'Taller de Mindfulness y Concentración', NULL, 'Centro de Meditación', 'Técnicas de atención plena'),
('2025-12-04', '14:00:00.000000', 7, 4, 459, 8, 'Charla de Primeros Auxilios Psicológicos', NULL, 'Centro Psicológico', 'Atención inicial en crisis emocionales'),
('2025-10-03', '08:30:00.000000', 8, 4, 460, 2, 'Congreso de Innovación Tecnológica', NULL, 'Centro de Convenciones', 'Tecnologías disruptivas para empresas'),
('2025-10-10', '09:00:00.000000', 8, 4, 461, 5, 'Feria de Emprendimiento Digital', NULL, 'Plaza Mayor', 'Startups y emprendimiento digital'),
('2025-10-17', '14:00:00.000000', 8, 4, 462, 6, 'Workshop de Design Thinking', NULL, 'Hotel Hilton', 'Metodología de diseño centrado en usuario'),
('2025-10-24', '10:00:00.000000', 8, 4, 463, 7, 'Conferencia de Economía Circular', NULL, 'Universidad Tecnológica', 'Sostenibilidad y economía circular'),
('2025-10-31', '16:00:00.000000', 8, 4, 464, 8, 'Networking Sector Tecnológico', NULL, 'Club Tecnológico', 'Conexiones en el sector tech'),
('2025-11-07', '08:30:00.000000', 8, 4, 465, 2, 'Congreso de Marketing de Influencers', NULL, 'Centro de Convenciones', 'Marketing con influencers y creadores'),
('2025-11-14', '09:00:00.000000', 8, 4, 466, 5, 'Feria de Servicios Financieros', NULL, 'Centro de Exposiciones', 'Servicios bancarios y financieros'),
('2025-11-21', '14:00:00.000000', 8, 4, 467, 6, 'Workshop de Ventas Consultivas', NULL, 'Cámara de Comercio', 'Ventas basadas en consultoría'),
('2025-11-28', '10:00:00.000000', 8, 4, 468, 7, 'Conferencia de Transformación Digital', NULL, 'Universidad de Cartagena', 'Digitalización empresarial'),
('2025-12-05', '16:00:00.000000', 8, 4, 469, 8, 'Networking Sector Creativo', NULL, 'Club de Creativos', 'Conexiones en industrias creativas'),
('2025-10-04', '20:00:00.000000', 9, 4, 470, 2, 'Festival de Música Tropical', NULL, 'Playa de Marbella', 'Ritmos tropicales caribeños'),
('2025-10-11', '21:00:00.000000', 9, 4, 471, 5, 'Noche de Boleros Románticos', NULL, 'Café del Mar', 'Los boleros más sentimentales'),
('2025-10-18', '19:00:00.000000', 9, 4, 472, 6, 'Concierto de Música Celta Irlandesa', NULL, 'Parque del Periodista', 'Música tradicional irlandesa'),
('2025-10-25', '20:30:00.000000', 9, 4, 473, 7, 'Festival de Música Electrónica Trance', NULL, 'Jardines del Club', 'Trance y música electrónica'),
('2025-11-01', '22:00:00.000000', 9, 4, 474, 8, 'Fiesta de Música Reggaeton', NULL, 'Discoteca La Terraza', 'Lo mejor del reggaeton actual'),
('2025-11-08', '19:00:00.000000', 9, 4, 475, 2, 'Concierto de Música Clásica Italiana', NULL, 'Iglesia San Pedro Claver', 'Obras de compositores italianos'),
('2025-11-15', '20:00:00.000000', 9, 4, 476, 5, 'Noche de Jazz Tradicional', NULL, 'Bar Jazz Club', 'Jazz clásico y tradicional'),
('2025-11-22', '21:00:00.000000', 9, 4, 477, 6, 'Festival de Música Folclórica Mexicana', NULL, 'Plaza México', 'Música tradicional mexicana'),
('2025-11-29', '19:30:00.000000', 9, 4, 478, 7, 'Concierto de Música Experimental Sonora', NULL, 'Auditorio Universidad', 'Exploración sonora experimental'),
('2025-12-06', '20:30:00.000000', 9, 4, 479, 8, 'Noche de Música Lounge Jazz', NULL, 'Sky Bar', 'Fusión de lounge y jazz'),
('2025-10-05', '18:00:00.000000', 10, 4, 480, 2, 'Concierto de Alabanza y Adoración', NULL, 'Iglesia San Roque', 'Música de adoración contemporánea'),
('2025-10-12', '10:00:00.000000', 10, 4, 481, 5, 'Retiro de Sanación Interior', NULL, 'Monasterio La Popa', 'Jornada de sanación espiritual'),
('2025-10-19', '19:00:00.000000', 10, 4, 482, 6, 'Velada Eucarística de Sanación', NULL, 'Catedral Metropolitana', 'Sanación a través de la eucaristía'),
('2025-10-26', '17:00:00.000000', 10, 4, 483, 7, 'Festival de Coros Infantiles', NULL, 'Basílica Menor', 'Coros de niños cantando alabanzas'),
('2025-11-02', '16:00:00.000000', 10, 4, 484, 8, 'Celebración de Todos los Santos', NULL, 'Santuario María Auxiliadora', 'Fiesta de todos los santos'),
('2025-11-09', '18:00:00.000000', 10, 4, 485, 2, 'Concierto de Música Sacra Contemporánea', NULL, 'Iglesia Santo Toribio', 'Música religiosa moderna'),
('2025-11-16', '10:00:00.000000', 10, 4, 486, 5, 'Peregrinación Mariana Nocturna', NULL, 'Cerro de La Popa', 'Peregrinación nocturna mariana'),
('2025-11-23', '19:00:00.000000', 10, 4, 487, 6, 'Velada del Sagrado Corazón', NULL, 'Santuario María Auxiliadora', 'Devoción al sagrado corazón'),
('2025-11-30', '17:00:00.000000', 10, 4, 488, 7, 'Festival de Coros de Adviento', NULL, 'Teatro Adolfo Mejía', 'Coros preparando la navidad'),
('2025-12-07', '16:00:00.000000', 10, 4, 489, 8, 'Celebración de la Inmaculada', NULL, 'Parroquia San Pedro', 'Fiesta de la inmaculada concepción'),
('2025-10-06', '09:00:00.000000', 11, 4, 490, 2, 'Hackathon de Inteligencia Artificial', NULL, 'Centro de Innovación', 'Desarrollo de soluciones con IA'),
('2025-10-13', '15:00:00.000000', 11, 4, 491, 5, 'Conferencia de Blockchain Empresarial', NULL, 'Universidad de Cartagena', 'Blockchain para negocios'),
('2025-10-20', '11:00:00.000000', 11, 4, 492, 6, 'Expo Realidad Mixta', NULL, 'Coliseo Cubierto', 'Realidad aumentada y virtual combinadas'),
('2025-10-27', '18:00:00.000000', 11, 4, 493, 7, 'Taller de Desarrollo de Apps Móviles', NULL, 'Centro Tecnológico', 'Creación de aplicaciones móviles'),
('2025-11-03', '14:00:00.000000', 11, 4, 494, 8, 'Feria de E-sports y Gaming', NULL, 'Mall Plaza', 'Torneos de videojuegos y gaming'),
('2025-11-10', '09:00:00.000000', 11, 4, 495, 2, 'Hackathon de Ciberseguridad', NULL, 'Centro de Innovación', 'Desafíos de seguridad informática'),
('2025-11-17', '15:00:00.000000', 11, 4, 496, 5, 'Conferencia de IoT Industrial', NULL, 'Universidad Tecnológica', 'Internet de las cosas industrial'),
('2025-11-24', '11:00:00.000000', 11, 4, 497, 6, 'Expo Drones de Carreras', NULL, 'Centro de Convenciones', 'Competencia de drones de carreras'),
('2025-12-01', '18:00:00.000000', 11, 4, 498, 7, 'Taller de Machine Learning Práctico', NULL, 'Centro Tecnológico', 'Aprendizaje automático aplicado'),
('2025-12-08', '14:00:00.000000', 11, 4, 499, 8, 'Feria de Tecnología Educativa', NULL, 'Mall Plaza', 'Tecnología para la educación'),
('2025-09-30', '10:00:00.000000', 1, 4, 500, 2, 'Festival de Otoño Familiar', NULL, 'Parque de Bolívar', 'Actividades temáticas de otoño para toda la familia'),
('2025-10-07', '15:00:00.000000', 1, 4, 501, 5, 'Taller de Halloween para Niños', NULL, 'Centro Comercial Caribe Plaza', 'Preparación segura para Halloween'),
('2025-10-14', '11:00:00.000000', 1, 4, 502, 6, 'Show de Magia de Halloween', NULL, 'Teatro Miramar', 'Magia y misterio para la temporada'),
('2025-10-21', '16:00:00.000000', 1, 4, 503, 7, 'Feria de Ciencias de Halloween', NULL, 'Colegio San Lucas', 'Experimentos científicos espeluznantes'),
('2025-10-28', '14:00:00.000000', 1, 4, 504, 8, 'Concurso de Disfraces Familiar', NULL, 'Plaza de la Paz', 'Competencia de disfraces en familia'),
('2025-11-04', '17:00:00.000000', 1, 4, 505, 2, 'Noche de Juegos de Mesa', NULL, 'Centro Comercial La Serrezuela', 'Juegos de mesa para todas las edades'),
('2025-11-11', '10:00:00.000000', 1, 4, 506, 5, 'Taller de Manualidades Navideñas', NULL, 'Casa de la Cultura', 'Preparando la navidad con manualidades'),
('2025-11-18', '15:00:00.000000', 1, 4, 507, 6, 'Festival de Villancicos Infantiles', NULL, 'Plaza de Santo Domingo', 'Niños cantando villancicos'),
('2025-11-25', '11:00:00.000000', 1, 4, 508, 7, 'Show de Títeres Navideños', NULL, 'Teatro Heredia', 'Títeres con temática navideña'),
('2025-12-02', '16:00:00.000000', 1, 4, 509, 8, 'Feria de Juguetes Educativos', NULL, 'Centro de Convenciones', 'Juguetes que enseñan y divierten'),
('2025-09-28', '20:00:00.000000', 2, 4, 510, 2, 'Concierto de Rock Progresivo', NULL, 'Teatro Adolfo Mejía', 'Rock progresivo y experimental'),
('2025-10-05', '21:00:00.000000', 2, 4, 511, 5, 'Noche de Salsa Romántica', NULL, 'Casa de la Salsa', 'Salsa para enamorados'),
('2025-10-12', '19:00:00.000000', 2, 4, 512, 6, 'Festival de Música del Pacífico', NULL, 'Plaza de la Aduana', 'Música tradicional del Pacífico colombiano'),
('2025-10-19', '20:30:00.000000', 2, 4, 513, 7, 'Concierto de Música Ambient', NULL, 'Jardín Botánico', 'Música ambiental y relajante'),
('2025-10-26', '22:00:00.000000', 2, 4, 514, 8, 'Fiesta de Música House', NULL, 'Discoteca La Movida', 'House music toda la noche'),
('2025-11-02', '19:00:00.000000', 2, 4, 515, 2, 'Concierto de Música Clásica Rusa', NULL, 'Iglesia San Pedro Claver', 'Obras de compositores rusos'),
('2025-11-09', '20:00:00.000000', 2, 4, 516, 5, 'Noche de Jazz Contemporáneo', NULL, 'Bar Jazz Club', 'Jazz moderno y vanguardista'),
('2025-11-16', '21:00:00.000000', 2, 4, 517, 6, 'Festival de Música Tradicional China', NULL, 'Centro Cultural', 'Música milenaria china'),
('2025-11-23', '19:30:00.000000', 2, 4, 518, 7, 'Concierto de Música Experimental', NULL, 'Auditorio Universidad', 'Sonidos experimentales y vanguardistas'),
('2025-11-30', '20:30:00.000000', 2, 4, 519, 8, 'Noche de Música Chillout', NULL, 'Sky Bar', 'Música relajante para desconectar'),
('2025-09-29', '08:00:00.000000', 3, 4, 520, 2, 'Maratón Nocturno Familiar', NULL, 'Avenida San Martín', 'Carrera nocturna para toda la familia'),
('2025-10-06', '07:00:00.000000', 3, 4, 521, 5, 'Torneo de Pádel Dobles Mixto', NULL, 'Club de Pádel', 'Competencia mixta de pádel'),
('2025-10-13', '16:00:00.000000', 3, 4, 522, 6, 'Campeonato de Ajedrez por Equipos', NULL, 'Casa del Ajedrez', 'Competencia grupal de ajedrez'),
('2025-10-20', '09:00:00.000000', 3, 4, 523, 7, 'Competencia de Natación Master', NULL, 'Piscina Olímpica', 'Pruebas para nadadores veteranos'),
('2025-10-27', '14:00:00.000000', 3, 4, 524, 8, 'Torneo de Fútbol Playa Femenino', NULL, 'Playa de Castillogrande', 'Competencia femenina de fútbol playa'),
('2025-11-03', '08:00:00.000000', 3, 4, 525, 2, 'Triatlón Familiar', NULL, 'Playas de Marbella', 'Triatlón adaptado para familias'),
('2025-11-10', '07:00:00.000000', 3, 4, 526, 5, 'Torneo de Baloncesto 3x3 Juvenil', NULL, 'Canchas de San Diego', 'Competencia para jóvenes');
INSERT INTO `evento` (`fecha`, `hora`, `idCategoria`, `idEstado`, `idEvento`, `idOrganizador`, `titulo`, `foto`, `lugar`, `descripcion`) VALUES
('2025-11-17', '16:00:00.000000', 3, 4, 527, 6, 'Campeonato de Tenis de Mesa Master', NULL, 'Coliseo de Combate', 'Competencia para mayores de 40 años'),
('2025-11-24', '09:00:00.000000', 3, 4, 528, 7, 'Competencia de Ciclismo Urbano', NULL, 'Centro Histórico', 'Recorrido urbano en bicicleta'),
('2025-12-01', '14:00:00.000000', 3, 4, 529, 8, 'Torneo de Voleibol Playa Juvenil', NULL, 'Playa de Bocagrande', 'Competencia para jóvenes'),
('2025-09-30', '18:00:00.000000', 5, 4, 530, 2, 'Exposición de Arte Surrealista', NULL, 'Museo de Arte Moderno', 'Arte surrealista y onírico'),
('2025-10-07', '19:00:00.000000', 5, 4, 531, 5, 'Noche de Teatro de Comedia', NULL, 'Teatro Heredia', 'Comedia y humor en escena'),
('2025-10-14', '17:00:00.000000', 5, 4, 532, 6, 'Festival de Danzas Caribeñas', NULL, 'Plaza de los Coches', 'Danzas tradicionales del Caribe'),
('2025-10-21', '20:00:00.000000', 5, 4, 533, 7, 'Concierto de Música Clásica Alemana', NULL, 'Iglesia Santo Domingo', 'Obras de compositores alemanes'),
('2025-10-28', '16:00:00.000000', 5, 4, 534, 8, 'Muestra de Cine de Terror', NULL, 'Cinemateca Distrital', 'Películas de terror y suspenso'),
('2025-11-04', '18:00:00.000000', 5, 4, 535, 2, 'Exposición de Fotografía Urbana', NULL, 'Galería Urbana', 'Fotografía de la vida en la ciudad'),
('2025-11-11', '19:00:00.000000', 5, 4, 536, 5, 'Noche de Teatro Dramático', NULL, 'Teatro al Aire Libre', 'Drama y emociones intensas'),
('2025-11-18', '17:00:00.000000', 5, 4, 537, 6, 'Festival de Danzas Contemporáneas', NULL, 'Plaza de la Trinidad', 'Danza moderna y contemporánea'),
('2025-11-25', '20:00:00.000000', 5, 4, 538, 7, 'Concierto de Música Tradicional Japonesa', NULL, 'Centro Cultural', 'Música tradicional del Japón'),
('2025-12-02', '16:00:00.000000', 5, 4, 539, 8, 'Muestra de Cine Navideño', NULL, 'Cinemateca Distrital', 'Películas con temática navideña'),
('2025-10-01', '12:00:00.000000', 6, 4, 540, 2, 'Festival de Comida Española', NULL, 'Plaza España', 'Sabores auténticos de España'),
('2025-10-08', '19:00:00.000000', 6, 4, 541, 5, 'Cena de Cocina Mediterránea', NULL, 'Restaurante Mediterráneo', 'Menú degustación mediterráneo'),
('2025-10-15', '11:00:00.000000', 6, 4, 542, 6, 'Mercado de Productos Orgánicos', NULL, 'Parque del Centenario', 'Productos ecológicos y naturales'),
('2025-10-22', '18:00:00.000000', 6, 4, 543, 7, 'Festival de la Hamburguesa Gourmet', NULL, 'Plaza de Bolívar', 'Hamburguesas gourmet creativas'),
('2025-10-29', '14:00:00.000000', 6, 4, 544, 8, 'Taller de Coctelería Tropical', NULL, 'Bar Tropical', 'Cocteles con frutas tropicales'),
('2025-11-05', '12:00:00.000000', 6, 4, 545, 2, 'Festival de Comida Argentina', NULL, 'Restaurante Argentino', 'Carnes y sabores argentinos'),
('2025-11-12', '19:00:00.000000', 6, 4, 546, 5, 'Cena de Cocina Peruana', NULL, 'Restaurante Peruano', 'Menú degustación peruano'),
('2025-11-19', '11:00:00.000000', 6, 4, 547, 6, 'Mercado de Panes Artesanales', NULL, 'Plaza de los Panes', 'Panes de diferentes regiones'),
('2025-11-26', '18:00:00.000000', 6, 4, 548, 7, 'Festival de la Pizza Artesanal', NULL, 'Pizzería Tradicional', 'Pizzas artesanales creativas'),
('2025-12-03', '14:00:00.000000', 6, 4, 549, 8, 'Taller de Repostería Navideña', NULL, 'Pastelería Navideña', 'Postres típicos de navidad'),
('2025-10-02', '09:00:00.000000', 7, 4, 550, 2, 'Taller de Programación Web', NULL, 'Centro de Computación', 'Desarrollo web para principiantes'),
('2025-10-09', '15:00:00.000000', 7, 4, 551, 5, 'Seminario de Marketing Digital Avanzado', NULL, 'Centro de Convenciones', 'Estrategias digitales avanzadas'),
('2025-10-16', '10:00:00.000000', 7, 4, 552, 6, 'Curso de Italiano Básico', NULL, 'Instituto de Idiomas', 'Introducción al idioma italiano'),
('2025-10-23', '18:00:00.000000', 7, 4, 553, 7, 'Taller de Finanzas Personales', NULL, 'Biblioteca Distrital', 'Manejo inteligente del dinero'),
('2025-10-30', '14:00:00.000000', 7, 4, 554, 8, 'Charla de Nutrición Holística', NULL, 'Centro de Salud', 'Alimentación integral y saludable'),
('2025-11-06', '09:00:00.000000', 7, 4, 555, 2, 'Taller de Fotografía de Producto', NULL, 'Escuela de Fotografía', 'Fotografía comercial y publicitaria'),
('2025-11-13', '15:00:00.000000', 7, 4, 556, 5, 'Seminario de Liderazgo Transformacional', NULL, 'Universidad de Cartagena', 'Liderazgo que transforma'),
('2025-11-20', '10:00:00.000000', 7, 4, 557, 6, 'Curso de Chino Mandarín Básico', NULL, 'Instituto de Idiomas', 'Introducción al mandarín'),
('2025-11-27', '18:00:00.000000', 7, 4, 558, 7, 'Taller de Mindfulness y Concentración', NULL, 'Centro de Meditación', 'Técnicas de atención plena'),
('2025-12-04', '14:00:00.000000', 7, 4, 559, 8, 'Charla de Primeros Auxilios Psicológicos', NULL, 'Centro Psicológico', 'Atención inicial en crisis emocionales'),
('2025-10-03', '08:30:00.000000', 8, 4, 560, 2, 'Congreso de Innovación Tecnológica', NULL, 'Centro de Convenciones', 'Tecnologías disruptivas para empresas'),
('2025-10-10', '09:00:00.000000', 8, 4, 561, 5, 'Feria de Emprendimiento Digital', NULL, 'Plaza Mayor', 'Startups y emprendimiento digital'),
('2025-10-17', '14:00:00.000000', 8, 4, 562, 6, 'Workshop de Design Thinking', NULL, 'Hotel Hilton', 'Metodología de diseño centrado en usuario'),
('2025-10-24', '10:00:00.000000', 8, 4, 563, 7, 'Conferencia de Economía Circular', NULL, 'Universidad Tecnológica', 'Sostenibilidad y economía circular'),
('2025-10-31', '16:00:00.000000', 8, 4, 564, 8, 'Networking Sector Tecnológico', NULL, 'Club Tecnológico', 'Conexiones en el sector tech'),
('2025-11-07', '08:30:00.000000', 8, 4, 565, 2, 'Congreso de Marketing de Influencers', NULL, 'Centro de Convenciones', 'Marketing con influencers y creadores'),
('2025-11-14', '09:00:00.000000', 8, 4, 566, 5, 'Feria de Servicios Financieros', NULL, 'Centro de Exposiciones', 'Servicios bancarios y financieros'),
('2025-11-21', '14:00:00.000000', 8, 4, 567, 6, 'Workshop de Ventas Consultivas', NULL, 'Cámara de Comercio', 'Ventas basadas en consultoría'),
('2025-11-28', '10:00:00.000000', 8, 4, 568, 7, 'Conferencia de Transformación Digital', NULL, 'Universidad de Cartagena', 'Digitalización empresarial'),
('2025-12-05', '16:00:00.000000', 8, 4, 569, 8, 'Networking Sector Creativo', NULL, 'Club de Creativos', 'Conexiones en industrias creativas'),
('2025-10-04', '20:00:00.000000', 9, 4, 570, 2, 'Festival de Música Tropical', NULL, 'Playa de Marbella', 'Ritmos tropicales caribeños'),
('2025-10-11', '21:00:00.000000', 9, 4, 571, 5, 'Noche de Boleros Románticos', NULL, 'Café del Mar', 'Los boleros más sentimentales'),
('2025-10-18', '19:00:00.000000', 9, 4, 572, 6, 'Concierto de Música Celta Irlandesa', NULL, 'Parque del Periodista', 'Música tradicional irlandesa'),
('2025-10-25', '20:30:00.000000', 9, 4, 573, 7, 'Festival de Música Electrónica Trance', NULL, 'Jardines del Club', 'Trance y música electrónica'),
('2025-11-01', '22:00:00.000000', 9, 4, 574, 8, 'Fiesta de Música Reggaeton', NULL, 'Discoteca La Terraza', 'Lo mejor del reggaeton actual'),
('2025-11-08', '19:00:00.000000', 9, 4, 575, 2, 'Concierto de Música Clásica Italiana', NULL, 'Iglesia San Pedro Claver', 'Obras de compositores italianos'),
('2025-11-15', '20:00:00.000000', 9, 4, 576, 5, 'Noche de Jazz Tradicional', NULL, 'Bar Jazz Club', 'Jazz clásico y tradicional'),
('2025-11-22', '21:00:00.000000', 9, 4, 577, 6, 'Festival de Música Folclórica Mexicana', NULL, 'Plaza México', 'Música tradicional mexicana'),
('2025-11-29', '19:30:00.000000', 9, 4, 578, 7, 'Concierto de Música Experimental Sonora', NULL, 'Auditorio Universidad', 'Exploración sonora experimental'),
('2025-12-06', '20:30:00.000000', 9, 4, 579, 8, 'Noche de Música Lounge Jazz', NULL, 'Sky Bar', 'Fusión de lounge y jazz'),
('2025-10-05', '18:00:00.000000', 10, 4, 580, 2, 'Concierto de Alabanza y Adoración', NULL, 'Iglesia San Roque', 'Música de adoración contemporánea'),
('2025-10-12', '10:00:00.000000', 10, 4, 581, 5, 'Retiro de Sanación Interior', NULL, 'Monasterio La Popa', 'Jornada de sanación espiritual'),
('2025-10-19', '19:00:00.000000', 10, 4, 582, 6, 'Velada Eucarística de Sanación', NULL, 'Catedral Metropolitana', 'Sanación a través de la eucaristía'),
('2025-10-26', '17:00:00.000000', 10, 4, 583, 7, 'Festival de Coros Infantiles', NULL, 'Basílica Menor', 'Coros de niños cantando alabanzas'),
('2025-11-02', '16:00:00.000000', 10, 4, 584, 8, 'Celebración de Todos los Santos', NULL, 'Santuario María Auxiliadora', 'Fiesta de todos los santos'),
('2025-11-09', '18:00:00.000000', 10, 4, 585, 2, 'Concierto de Música Sacra Contemporánea', NULL, 'Iglesia Santo Toribio', 'Música religiosa moderna'),
('2025-11-16', '10:00:00.000000', 10, 4, 586, 5, 'Peregrinación Mariana Nocturna', NULL, 'Cerro de La Popa', 'Peregrinación nocturna mariana'),
('2025-11-23', '19:00:00.000000', 10, 4, 587, 6, 'Velada del Sagrado Corazón', NULL, 'Santuario María Auxiliadora', 'Devoción al sagrado corazón'),
('2025-11-30', '17:00:00.000000', 10, 4, 588, 7, 'Festival de Coros de Adviento', NULL, 'Teatro Adolfo Mejía', 'Coros preparando la navidad'),
('2025-12-07', '16:00:00.000000', 10, 4, 589, 8, 'Celebración de la Inmaculada', NULL, 'Parroquia San Pedro', 'Fiesta de la inmaculada concepción'),
('2025-10-06', '09:00:00.000000', 11, 4, 590, 2, 'Hackathon de Inteligencia Artificial', NULL, 'Centro de Innovación', 'Desarrollo de soluciones con IA'),
('2025-10-13', '15:00:00.000000', 11, 4, 591, 5, 'Conferencia de Blockchain Empresarial', NULL, 'Universidad de Cartagena', 'Blockchain para negocios'),
('2025-10-20', '11:00:00.000000', 11, 4, 592, 6, 'Expo Realidad Mixta', NULL, 'Coliseo Cubierto', 'Realidad aumentada y virtual combinadas'),
('2025-10-27', '18:00:00.000000', 11, 4, 593, 7, 'Taller de Desarrollo de Apps Móviles', NULL, 'Centro Tecnológico', 'Creación de aplicaciones móviles'),
('2025-11-03', '14:00:00.000000', 11, 4, 594, 8, 'Feria de E-sports y Gaming', NULL, 'Mall Plaza', 'Torneos de videojuegos y gaming'),
('2025-11-10', '09:00:00.000000', 11, 4, 595, 2, 'Hackathon de Ciberseguridad', NULL, 'Centro de Innovación', 'Desafíos de seguridad informática'),
('2025-11-17', '15:00:00.000000', 11, 4, 596, 5, 'Conferencia de IoT Industrial', NULL, 'Universidad Tecnológica', 'Internet de las cosas industrial'),
('2025-11-24', '11:00:00.000000', 11, 4, 597, 6, 'Expo Drones de Carreras', NULL, 'Centro de Convenciones', 'Competencia de drones de carreras'),
('2025-12-01', '18:00:00.000000', 11, 4, 598, 7, 'Taller de Machine Learning Práctico', NULL, 'Centro Tecnológico', 'Aprendizaje automático aplicado'),
('2025-12-08', '14:00:00.000000', 11, 4, 599, 8, 'Feria de Tecnología Educativa', NULL, 'Mall Plaza', 'Tecnología para la educación'),
('2025-12-06', '17:30:00.000000', 3, 4, 600, 2, 'Macth MLB ALL STAR', 'ac33b8e4-bab4-439f-8aef-1eaccc213c24.jpg', 'estadio 11 de noviembre', 'el juego de las estrellas de la mlb llega a cartagena'),
('2025-10-03', '08:00:00.000000', 3, 4, 601, 21, 'Carrera Atlética 5K Nocturna', NULL, 'Avenida San Martín', 'Carrera nocturna por las principales avenidas de la ciudad'),
('2025-10-10', '19:00:00.000000', 9, 4, 602, 21, 'Festival de Música Vallenata', NULL, 'Paradero Turístico', 'Los mejores acordeoneros del vallenato en un solo lugar'),
('2025-10-17', '16:00:00.000000', 6, 4, 603, 21, 'Taller de Coctelería Tropical', NULL, 'Bar Donde Fidel', 'Aprende a preparar los cocteles más refrescantes del Caribe'),
('2025-10-24', '20:30:00.000000', 2, 4, 604, 21, 'Concierto de Rock Nacional', NULL, 'Teatro al Aire Libre', 'Las bandas de rock colombiano más representativas'),
('2025-11-01', '14:00:00.000000', 1, 4, 605, 21, 'Día de Juegos Tradicionales', NULL, 'Plaza de la Trinidad', 'Rondas, trompo y yo-yo para revivir la nostalgia'),
('2025-11-08', '18:00:00.000000', 5, 4, 606, 21, 'Exposición de Arte Naif', NULL, 'Galería del Centro', 'Arte ingenuo y colorido de artistas regionales'),
('2025-11-15', '19:30:00.000000', 9, 4, 607, 21, 'Noche de Música Romántica', NULL, 'Hotel Caribe', 'Baladas y canciones de amor para parejas'),
('2025-11-22', '10:00:00.000000', 7, 4, 608, 21, 'Workshop de Podcasting', NULL, 'Estudios de Radio', 'Aprende a crear y producir tu propio podcast'),
('2025-11-29', '17:00:00.000000', 3, 4, 609, 21, 'Torneo de Fútbol Sala', NULL, 'Coliseo de Combate', 'Competencia de fútbol sala categorías libre'),
('2025-12-06', '20:00:00.000000', 2, 4, 610, 21, 'Festival de Hip Hop Urbano', NULL, 'Plaza de la Matuna', 'Battles de rap y exhibiciones de breakdance'),
('2025-01-10', '19:00:00.000000', 9, 4, 611, 22, 'Concierto de Música Tropical', NULL, 'Club de Pesca', 'Orquestas de salsa, merengue y cumbia para bailar'),
('2025-01-18', '15:00:00.000000', 6, 4, 612, 22, 'Festival de Comida Mexicana', NULL, 'Zona Gastronómica', 'Tacos, burritos y auténticos sabores mexicanos'),
('2025-01-26', '20:30:00.000000', 2, 4, 613, 22, 'Noche de Jazz Fusión', NULL, 'Café del Mar', 'Fusión de jazz con ritmos caribeños en la muralla'),
('2025-02-03', '11:00:00.000000', 1, 4, 614, 22, 'Taller de Manualidades Infantiles', NULL, 'Centro Comunitario', 'Creatividad y diversión para los más pequeños'),
('2025-02-11', '18:00:00.000000', 5, 4, 615, 22, 'Exposición de Acuarelas', NULL, 'Casa de la Cultura', 'Técnicas de acuarela en paisajes cartageneros'),
('2025-02-19', '16:00:00.000000', 3, 4, 616, 22, 'Competencia de Ciclismo Urbano', NULL, 'Circuito Ciudad', 'Recorrido por el centro histórico en bicicleta'),
('2025-02-27', '19:30:00.000000', 9, 4, 617, 22, 'Concierto de Música Indie', NULL, 'Bar La Cueva', 'Bandas independientes con propuestas innovadoras'),
('2025-03-07', '14:00:00.000000', 7, 4, 618, 22, 'Curso de Mixología Básica', NULL, 'Escuela de Barman', 'Fundamentos para preparar cocteles clásicos'),
('2025-03-15', '20:00:00.000000', 2, 4, 619, 22, 'Festival de Sonidos Caribeños', NULL, 'Plaza de los Coches', 'Fusión de ritmos tradicionales del Caribe'),
('2025-03-23', '17:00:00.000000', 1, 4, 620, 22, 'Día de Película Familiar', NULL, 'Cine Colombia', 'Matiné especial con películas para toda la familia'),
('2025-04-02', '19:00:00.000000', 6, 4, 621, 23, 'Cena Gourmet Maridaje', NULL, 'Restaurante Club de Pesca', 'Menú degustación con maridaje de vinos seleccionados'),
('2025-04-10', '16:00:00.000000', 3, 4, 622, 23, 'Torneo de Pádel', NULL, 'Club de Pádel Cartagena', 'Competencia de pádel en todas las categorías'),
('2025-04-18', '20:30:00.000000', 9, 4, 623, 23, 'Noche de Música Brasileña', NULL, 'Plaza Santo Domingo', 'Bossa nova, samba y ritmos de Brasil'),
('2025-04-26', '11:00:00.000000', 1, 4, 624, 23, 'Feria de Ciencia y Tecnología', NULL, 'Colegio Mayor de Bolívar', 'Proyectos innovadores de estudiantes locales'),
('2025-05-04', '18:00:00.000000', 5, 4, 625, 23, 'Exposición de Fotografía Callejera', NULL, 'Galería Urbana', 'Instantáneas de la vida cotidiana cartagenera'),
('2025-05-12', '15:00:00.000000', 7, 4, 626, 23, 'Taller de Panadería Artesanal', NULL, 'Panadería Tradicional', 'Aprende a hacer pan artesanal como los abuelos'),
('2025-05-20', '19:30:00.000000', 2, 4, 627, 23, 'Concierto de Trova Cubana', NULL, 'Casa de la Trova', 'Los mejores trovadores cubanos en Cartagena'),
('2025-05-28', '14:00:00.000000', 3, 4, 628, 23, 'Competencia de Natación en Aguas Abiertas', NULL, 'Bahía de Cartagena', 'Travesía nadando por la bahía cartagenera'),
('2025-06-05', '20:00:00.000000', 9, 4, 629, 23, 'Festival de Música Andina Colombiana', NULL, 'Teatro al Aire Libre', 'Guabina, torbellino y bambuco en un solo escenario'),
('2025-06-13', '17:00:00.000000', 6, 4, 630, 23, 'Taller de Cocina de Mar', NULL, 'Escuela de Cocina del Mar', 'Técnicas para preparar pescados y mariscos'),
('2025-06-21', '19:00:00.000000', 11, 4, 631, 24, 'Hackathon de Desarrollo Web', NULL, 'Universidad Tecnológica', 'Competencia de desarrollo web de 48 horas'),
('2025-06-29', '09:00:00.000000', 7, 4, 632, 24, 'Curso de Programación Python', NULL, 'Centro de Innovación', 'Aprende programación desde cero con Python'),
('2025-07-07', '20:30:00.000000', 2, 4, 633, 24, 'Concierto de Música Electrónica Experimental', NULL, 'Warehouse Club', 'Sonidos electrónicos innovadores y vanguardistas'),
('2025-07-15', '16:00:00.000000', 3, 4, 634, 24, 'Torneo de eSports FIFA', NULL, 'Gaming Center', 'Competencia de FIFA para gamers profesionales'),
('2025-07-23', '18:00:00.000000', 11, 4, 635, 24, 'Conferencia de Blockchain', NULL, 'Centro de Convenciones', 'Aplicaciones prácticas de blockchain en negocios'),
('2025-07-31', '14:00:00.000000', 7, 4, 636, 24, 'Workshop de Realidad Virtual', NULL, 'Laboratorio VR', 'Experiencias inmersivas con realidad virtual'),
('2025-08-08', '19:30:00.000000', 9, 4, 637, 24, 'Festival de Synthwave', NULL, 'Discoteca La Escollera', 'Música synthwave y retrowave en ambiente futurista'),
('2025-08-16', '11:00:00.000000', 1, 4, 638, 24, 'Taller de Robótica para Niños', NULL, 'Museo de Ciencia', 'Introducción a la robótica educativa para niños'),
('2025-08-24', '20:00:00.000000', 2, 4, 639, 24, 'Concierto de DJs Internacionales', NULL, 'Playa Hollywood', 'DJs de talla mundial en la playa más exclusiva'),
('2025-09-01', '17:00:00.000000', 11, 4, 640, 24, 'Expo de Startups Tecnológicas', NULL, 'ParqueSoft Cartagena', 'Emprendimientos tecnológicos innovadores'),
('2025-09-09', '19:00:00.000000', 8, 4, 641, 25, 'Conferencia de Ventas y Negociación', NULL, 'Hotel Dann Carlton', 'Estrategias efectivas de ventas y cierre de negocios'),
('2025-09-17', '08:30:00.000000', 7, 4, 642, 25, 'Seminario de Finanzas Personales', NULL, 'Cámara de Comercio', 'Aprende a manejar tus finanzas de manera inteligente'),
('2025-09-25', '20:30:00.000000', 4, 6, 643, 25, 'Cena de Aniversario Corporativo', NULL, 'Club de Ejecutivos', 'Celebración exclusiva para socios y directivos'),
('2025-10-03', '16:00:00.000000', 3, 4, 644, 25, 'Torneo de Golf Empresarial', NULL, 'Club de Golf Karibana', 'Competencia de golf para networking empresarial'),
('2025-10-11', '18:00:00.000000', 8, 4, 645, 25, 'Workshop de Oratoria Ejecutiva', NULL, 'Centro de Capacitación', 'Mejora tus habilidades de speaking en público'),
('2025-10-19', '19:30:00.000000', 4, 6, 646, 25, 'Coctel de Negocios Internacionales', NULL, 'Hotel Intercontinental', 'Networking con empresarios internacionales'),
('2025-10-27', '14:00:00.000000', 7, 4, 647, 25, 'Curso de Excel Avanzado', NULL, 'Instituto de Formación', 'Funciones avanzadas de Excel para profesionales'),
('2025-11-04', '20:00:00.000000', 8, 4, 648, 25, 'Foro de Inversiones', NULL, 'Bolsa de Valores', 'Oportunidades de inversión en el Caribe colombiano'),
('2025-11-12', '17:00:00.000000', 4, 6, 649, 25, 'Lanzamiento de Producto Exclusivo', NULL, 'Showroom Empresarial', 'Presentación de nuevo producto para clientes selectos'),
('2025-11-20', '19:00:00.000000', 8, 4, 650, 25, 'Seminario de Transformación Digital', NULL, 'Centro de Innovación', 'Adaptación digital para empresas tradicionales'),
('2025-11-28', '15:00:00.000000', 1, 4, 651, 26, 'Feria de Juegos de Mesa Familiares', NULL, 'Centro Recreacional', 'Juegos de mesa clásicos y modernos para todas las edades'),
('2025-12-06', '11:00:00.000000', 1, 4, 652, 26, 'Taller de Cocina en Familia', NULL, 'Escuela de Cocina Familiar', 'Padres e hijos cocinando juntos recetas divertidas'),
('2025-12-14', '16:00:00.000000', 1, 4, 653, 26, 'Picnic Navideño Familiar', NULL, 'Jardines del Castillo', 'Celebración navideña con actividades para toda la familia'),
('2025-12-22', '18:00:00.000000', 1, 4, 654, 26, 'Noche de Películas Familiares', NULL, 'Cine al Aire Libre', 'Proyección de clásicos familiares bajo las estrellas'),
('2025-12-30', '14:00:00.000000', 1, 4, 655, 26, 'Fiesta de Fin de Año Infantil', NULL, 'Club Infantil', 'Celebración segura de fin de año para los más pequeños'),
('2026-01-07', '17:00:00.000000', 1, 4, 656, 26, 'Taller de Manualidades Navideñas', NULL, 'Centro Artesanal', 'Crea tus propios adornos navideños en familia'),
('2026-01-15', '10:00:00.000000', 1, 4, 657, 26, 'Día de Deportes Familiares', NULL, 'Polideportivo', 'Competencias deportivas para padres e hijos'),
('2026-01-23', '19:00:00.000000', 1, 4, 658, 26, 'Noche de Karaoke Familiar', NULL, 'Centro Comunitario', 'Canta tus canciones favoritas en ambiente familiar'),
('2026-01-31', '15:00:00.000000', 1, 4, 659, 26, 'Feria de Ciencias para Familias', NULL, 'Museo de los Niños', 'Experimenta la ciencia de forma divertida en familia'),
('2026-02-08', '16:00:00.000000', 1, 4, 660, 26, 'Taller de Jardinería Familiar', NULL, 'Vivero Municipal', 'Aprende sobre plantas y jardinería con tus hijos');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `eventodeseados`
--

CREATE TABLE `eventodeseados` (
  `idEvento` bigint(20) NOT NULL,
  `idEventoDeseado` bigint(20) NOT NULL,
  `idUsuario` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `eventodeseados`
--

INSERT INTO `eventodeseados` (`idEvento`, `idEventoDeseado`, `idUsuario`) VALUES
(1, 1, 4),
(2, 2, 3),
(1, 3, 3);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `localidades`
--

CREATE TABLE `localidades` (
  `capacidad` int(11) DEFAULT NULL,
  `disponibles` int(11) DEFAULT NULL,
  `precio` decimal(10,2) DEFAULT NULL,
  `idEvento` bigint(20) NOT NULL,
  `idLocalidades` bigint(20) NOT NULL,
  `nombre` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `localidades`
--

INSERT INTO `localidades` (`capacidad`, `disponibles`, `precio`, `idEvento`, `idLocalidades`, `nombre`) VALUES
(600, 600, 0.00, 2, 1, 'General'),
(600, 600, 200000.00, 3, 2, 'General'),
(500, 500, 250000.00, 3, 4, 'Platino'),
(700, 696, 50000.00, 1, 5, 'General'),
(300, 300, 120000.00, 1, 6, 'Degustación VIP'),
(300, 300, 0.00, 4, 7, 'General'),
(200, 200, 20000.00, 5, 8, 'General'),
(50, 50, 50000.00, 5, 9, 'Preferencial'),
(200, 200, 0.00, 6, 10, 'General'),
(120, 120, 15000.00, 7, 11, 'General'),
(30, 30, 30000.00, 7, 12, 'Mesa VIP'),
(150, 150, 40000.00, 8, 13, 'General'),
(50, 50, 80000.00, 8, 14, 'VIP'),
(400, 400, 15000.00, 10, 17, 'General'),
(600, 600, 20000.00, 11, 18, 'General'),
(200, 200, 40000.00, 11, 19, 'Preferencial'),
(120, 120, 0.00, 12, 20, 'Invitados'),
(80, 80, 0.00, 13, 21, 'Invitados'),
(400, 400, 30000.00, 15, 23, 'General'),
(200, 200, 60000.00, 15, 24, 'Preferencial'),
(300, 300, 20000.00, 16, 25, 'General'),
(200, 200, 15000.00, 17, 26, 'General'),
(150, 150, 25000.00, 17, 27, 'Preferencial'),
(150, 150, 90000.00, 18, 28, 'General'),
(100, 100, 150000.00, 18, 29, 'VIP Degustación'),
(300, 300, 30000.00, 19, 30, 'General'),
(100, 100, 70000.00, 19, 31, 'Zona Premium'),
(350, 350, 20000.00, 20, 32, 'General'),
(250, 250, 30000.00, 22, 34, 'General'),
(50, 50, 70000.00, 22, 35, 'Preferencial'),
(150, 150, 60000.00, 23, 36, 'General'),
(400, 400, 120000.00, 24, 37, 'General'),
(100, 100, 250000.00, 24, 38, 'VIP'),
(300, 300, 0.00, 25, 39, 'Entrada Libre'),
(2000, 2000, 90000.00, 27, 41, 'General'),
(1000, 997, 180000.00, 27, 42, 'Platino'),
(900, 900, 50000.00, 28, 43, 'General'),
(600, 600, 120000.00, 28, 44, 'Preferencial'),
(1400, 1400, 70000.00, 29, 45, 'General'),
(600, 600, 150000.00, 29, 46, 'VIP'),
(1000, 1000, 0.00, 30, 47, 'General'),
(300, 300, 0.00, 31, 48, 'Participantes'),
(200, 200, 20000.00, 33, 51, 'General'),
(100, 100, 40000.00, 33, 52, 'Gamer Zone'),
(150, 150, 15000.00, 34, 53, 'General'),
(150, 150, 10000.00, 35, 54, 'General'),
(180, 180, 15000.00, 36, 55, 'General'),
(500, 500, 70000.00, 3, 57, 'bronce'),
(1500, 1491, 45000.00, 39, 58, 'general'),
(100, 100, 500000.00, 600, 60, 'Asientos VIP dugout'),
(2000, 2000, 150000.00, 600, 61, 'oriental alta'),
(2000, 2000, 300000.00, 600, 62, 'oriental baja'),
(2000, 2000, 150000.00, 600, 63, 'occidental alta'),
(2000, 2000, 300000.00, 600, 64, 'occidental baja');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `promocion`
--

CREATE TABLE `promocion` (
  `descuento` decimal(10,2) DEFAULT NULL,
  `fecha_final` date DEFAULT NULL,
  `fecha_inicio` date DEFAULT NULL,
  `idEvento` bigint(20) NOT NULL,
  `idPromocion` bigint(20) NOT NULL,
  `descripcion` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `promocion`
--

INSERT INTO `promocion` (`descuento`, `fecha_final`, `fecha_inicio`, `idEvento`, `idPromocion`, `descripcion`) VALUES
(20.00, '2025-12-02', '2025-11-21', 3, 1, 'descuento por preventa');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `roles`
--

CREATE TABLE `roles` (
  `idEstado` bigint(20) NOT NULL,
  `idRoles` bigint(20) NOT NULL,
  `descripcion` varchar(45) DEFAULT NULL,
  `nombre` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `roles`
--

INSERT INTO `roles` (`idEstado`, `idRoles`, `descripcion`, `nombre`) VALUES
(1, 1, 'se encargara de crear y publicar sus eventos', 'organizador'),
(1, 2, 'encargado de la gestion del sistema', 'administrador'),
(1, 3, 'usuario encargado de comprar', 'cliente');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tiquete`
--

CREATE TABLE `tiquete` (
  `idTiquete` int(11) NOT NULL,
  `idLocalidades` bigint(20) NOT NULL,
  `codigoQR` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tiquete`
--

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

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tiquete_compra`
--

CREATE TABLE `tiquete_compra` (
  `cantidad` int(11) DEFAULT NULL,
  `idCompra` int(11) NOT NULL,
  `idTiquete` int(11) NOT NULL,
  `idTiquete_Compra` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tiquete_compra`
--

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

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `idEstado` bigint(20) NOT NULL,
  `idRoles` bigint(20) NOT NULL,
  `idUsuario` bigint(20) NOT NULL,
  `apellido` varchar(45) DEFAULT NULL,
  `clave` varchar(45) DEFAULT NULL,
  `correo` varchar(45) DEFAULT NULL,
  `nombre` varchar(45) DEFAULT NULL,
  `telefono` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`idEstado`, `idRoles`, `idUsuario`, `apellido`, `clave`, `correo`, `nombre`, `telefono`) VALUES
(1, 2, 1, 'Tordecilla Faria', 'prueba123', 'eduardestf20@gmail.com', 'Eduard Santiago', '3046139087'),
(1, 1, 2, 'Torres Narvaez', 'holacarolina', 'angiecarolinatorresnarvaez@gmail.com', 'Angie Carolina', '3000005555'),
(1, 3, 3, 'Saez Agamez', 'memo', 'contacteljhonny@gmail.com', 'Jhonatan', '3029991144'),
(1, 3, 4, 'Bermudez Mora', 'holabermudez', 'alebermudezmora@gmail.com', 'Alejandra Sofia', '3008972233'),
(1, 1, 5, 'Martinez Lopez', 'carlos2025', 'carlosmartinez@gmail.com', 'Carlos Alberto', '3015678901'),
(1, 1, 6, 'Rodriguez Perez', 'maria123', 'mariarodriguez@hotmail.com', 'Maria Fernanda', '3102345678'),
(1, 1, 7, 'Garcia Silva', 'juanorg', 'juangarcia@outlook.com', 'Juan Carlos', '3209876543'),
(1, 1, 8, 'Mendez Castro', 'luisa456', 'luisamendez@gmail.com', 'Luisa Maria', '3186543210'),
(1, 1, 11, 'Saez Agamez', 'ale123', 'bermudez@gmail.com', 'Alejandra Mora', '323876973'),
(1, 3, 13, 'Saez Agamez', 'juanca@', 'juacamusic@gmail.com', 'Juan camilo', '3229098778'),
(1, 3, 14, 'ambuila cardenaz', 'jcambuila34', 'jcambuila@hotmail.com', 'Juan camilo', '3129092345'),
(1, 3, 16, 'monterroza romero', 'ricardo@romero', 'adrianricardo@gmal.com', 'adrian ricardo', '3210008990'),
(1, 1, 18, 'González Rojas', 'clave123', 'mariagonzalez@eventos.com', 'María Elena', '3101112233'),
(1, 1, 19, 'Ramírez Silva', 'eventos2025', 'carlosramirez@organizador.com', 'Carlos Andrés', '3152223344'),
(1, 1, 20, 'Hernández Castro', 'producer123', 'lauraproducciones@hotmail.com', 'Laura Patricia', '3203334455'),
(1, 1, 21, 'Díaz Mendoza', 'cultural456', 'artecultural@gmail.com', 'Roberto Antonio', '3184445566'),
(1, 1, 22, 'López Vargas', 'deportes789', 'deporteslopez@outlook.com', 'Fernando José', '3125556677'),
(1, 1, 23, 'Martínez Ruiz', 'musicfest', 'festivalesmusica@gmail.com', 'Sandra Milena', '3146667788'),
(1, 1, 24, 'Pérez Ortega', 'gourmet2025', 'eventosgourmet@hotmail.com', 'Gabriela Sofía', '3177778899'),
(1, 1, 25, 'Rojas Herrera', 'tecnologia23', 'techconferences@gmail.com', 'David Alejandro', '3198889900'),
(1, 1, 26, 'Castillo Navarro', 'empresas456', 'eventoscorporativos@outlook.com', 'Andrea Carolina', '3139990011'),
(1, 1, 27, 'Morales Jiménez', 'familia789', 'eventosfamiliares@gmail.com', 'Julián Esteban', '3110001122');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `valoracion`
--

CREATE TABLE `valoracion` (
  `calificacion` bigint(20) NOT NULL,
  `idCliente` bigint(20) NOT NULL,
  `idEvento` bigint(20) NOT NULL,
  `idValoracion` bigint(20) NOT NULL,
  `comentario` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `valoracion`
--

INSERT INTO `valoracion` (`calificacion`, `idCliente`, `idEvento`, `idValoracion`, `comentario`) VALUES
(5, 4, 39, 1, 'excelente concierto, la organizacion supo hacerlo y sin contratiempos'),
(5, 4, 1, 3, 'me gusto mucho, la organizacion se merece mas\r\neventos en la ciudad asi como este'),
(4, 4, 2, 4, 'la ciudad deberia estar mas organizada al momento de eventos como este. hay demasiadas filas de personas esperando por embarcaciones y genera desorden en la ciudad');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `categorias`
--
ALTER TABLE `categorias`
  ADD PRIMARY KEY (`idCategoria`);

--
-- Indices de la tabla `compra`
--
ALTER TABLE `compra`
  ADD PRIMARY KEY (`idCompra`),
  ADD KEY `FKt4dvqbger1sta3ejgipnh85jy` (`idCliente`);

--
-- Indices de la tabla `estados`
--
ALTER TABLE `estados`
  ADD PRIMARY KEY (`idEstado`);

--
-- Indices de la tabla `evento`
--
ALTER TABLE `evento`
  ADD PRIMARY KEY (`idEvento`),
  ADD KEY `FKfmowhedpeal4qygt915yvgfub` (`idCategoria`),
  ADD KEY `FKf7qa2ed92n0gkvoovda42rrpv` (`idEstado`),
  ADD KEY `FKbkd8irotp6lim3sqsn784l4fq` (`idOrganizador`);

--
-- Indices de la tabla `eventodeseados`
--
ALTER TABLE `eventodeseados`
  ADD PRIMARY KEY (`idEventoDeseado`),
  ADD KEY `FK5e8ly91b55u3x0et3aef0tp12` (`idEvento`),
  ADD KEY `FK95hvgbywcshmullas18g3ftwo` (`idUsuario`);

--
-- Indices de la tabla `localidades`
--
ALTER TABLE `localidades`
  ADD PRIMARY KEY (`idLocalidades`),
  ADD KEY `FKlmuoqu02n6o2qxe0ywdsdophi` (`idEvento`);

--
-- Indices de la tabla `promocion`
--
ALTER TABLE `promocion`
  ADD PRIMARY KEY (`idPromocion`),
  ADD KEY `FKk8vlb921i2dfvt2hwrk4rektt` (`idEvento`);

--
-- Indices de la tabla `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`idRoles`),
  ADD KEY `FKjb3pf1kxd4kojetorhd6m08gb` (`idEstado`);

--
-- Indices de la tabla `tiquete`
--
ALTER TABLE `tiquete`
  ADD PRIMARY KEY (`idTiquete`),
  ADD UNIQUE KEY `UKru3eka2ups6via3txrqsb9wa5` (`codigoQR`),
  ADD KEY `FK4hgam1a9bvwrp6vrhytkdd1qg` (`idLocalidades`);

--
-- Indices de la tabla `tiquete_compra`
--
ALTER TABLE `tiquete_compra`
  ADD PRIMARY KEY (`idTiquete_Compra`),
  ADD KEY `FKo0nbpdqwj01hqn8qarha3fchn` (`idCompra`),
  ADD KEY `FKls3780b1wjbws25dav40n22tq` (`idTiquete`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`idUsuario`),
  ADD UNIQUE KEY `UKhhpke604cqf41i0e64hndw4cu` (`correo`),
  ADD KEY `FKocbdeoy4v7xii161d6knyqt5v` (`idEstado`),
  ADD KEY `FK38sog00sb9t6oirea4051sll5` (`idRoles`);

--
-- Indices de la tabla `valoracion`
--
ALTER TABLE `valoracion`
  ADD PRIMARY KEY (`idValoracion`),
  ADD KEY `FKa4deqn8awu1ps0qkjsnjdwity` (`idCliente`),
  ADD KEY `FK9ws5edcs6t6nb18ywne5dierf` (`idEvento`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `categorias`
--
ALTER TABLE `categorias`
  MODIFY `idCategoria` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT de la tabla `compra`
--
ALTER TABLE `compra`
  MODIFY `idCompra` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `estados`
--
ALTER TABLE `estados`
  MODIFY `idEstado` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `evento`
--
ALTER TABLE `evento`
  MODIFY `idEvento` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=661;

--
-- AUTO_INCREMENT de la tabla `eventodeseados`
--
ALTER TABLE `eventodeseados`
  MODIFY `idEventoDeseado` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `localidades`
--
ALTER TABLE `localidades`
  MODIFY `idLocalidades` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=75;

--
-- AUTO_INCREMENT de la tabla `promocion`
--
ALTER TABLE `promocion`
  MODIFY `idPromocion` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `roles`
--
ALTER TABLE `roles`
  MODIFY `idRoles` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `tiquete`
--
ALTER TABLE `tiquete`
  MODIFY `idTiquete` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT de la tabla `tiquete_compra`
--
ALTER TABLE `tiquete_compra`
  MODIFY `idTiquete_Compra` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `idUsuario` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=28;

--
-- AUTO_INCREMENT de la tabla `valoracion`
--
ALTER TABLE `valoracion`
  MODIFY `idValoracion` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `compra`
--
ALTER TABLE `compra`
  ADD CONSTRAINT `FKt4dvqbger1sta3ejgipnh85jy` FOREIGN KEY (`idCliente`) REFERENCES `usuario` (`idUsuario`);

--
-- Filtros para la tabla `evento`
--
ALTER TABLE `evento`
  ADD CONSTRAINT `FKbkd8irotp6lim3sqsn784l4fq` FOREIGN KEY (`idOrganizador`) REFERENCES `usuario` (`idUsuario`),
  ADD CONSTRAINT `FKf7qa2ed92n0gkvoovda42rrpv` FOREIGN KEY (`idEstado`) REFERENCES `estados` (`idEstado`),
  ADD CONSTRAINT `FKfmowhedpeal4qygt915yvgfub` FOREIGN KEY (`idCategoria`) REFERENCES `categorias` (`idCategoria`);

--
-- Filtros para la tabla `eventodeseados`
--
ALTER TABLE `eventodeseados`
  ADD CONSTRAINT `FK5e8ly91b55u3x0et3aef0tp12` FOREIGN KEY (`idEvento`) REFERENCES `evento` (`idEvento`),
  ADD CONSTRAINT `FK95hvgbywcshmullas18g3ftwo` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`);

--
-- Filtros para la tabla `localidades`
--
ALTER TABLE `localidades`
  ADD CONSTRAINT `FKlmuoqu02n6o2qxe0ywdsdophi` FOREIGN KEY (`idEvento`) REFERENCES `evento` (`idEvento`);

--
-- Filtros para la tabla `promocion`
--
ALTER TABLE `promocion`
  ADD CONSTRAINT `FKk8vlb921i2dfvt2hwrk4rektt` FOREIGN KEY (`idEvento`) REFERENCES `evento` (`idEvento`);

--
-- Filtros para la tabla `roles`
--
ALTER TABLE `roles`
  ADD CONSTRAINT `FKjb3pf1kxd4kojetorhd6m08gb` FOREIGN KEY (`idEstado`) REFERENCES `estados` (`idEstado`);

--
-- Filtros para la tabla `tiquete`
--
ALTER TABLE `tiquete`
  ADD CONSTRAINT `FK4hgam1a9bvwrp6vrhytkdd1qg` FOREIGN KEY (`idLocalidades`) REFERENCES `localidades` (`idLocalidades`);

--
-- Filtros para la tabla `tiquete_compra`
--
ALTER TABLE `tiquete_compra`
  ADD CONSTRAINT `FKls3780b1wjbws25dav40n22tq` FOREIGN KEY (`idTiquete`) REFERENCES `tiquete` (`idTiquete`),
  ADD CONSTRAINT `FKo0nbpdqwj01hqn8qarha3fchn` FOREIGN KEY (`idCompra`) REFERENCES `compra` (`idCompra`);

--
-- Filtros para la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD CONSTRAINT `FK38sog00sb9t6oirea4051sll5` FOREIGN KEY (`idRoles`) REFERENCES `roles` (`idRoles`),
  ADD CONSTRAINT `FKocbdeoy4v7xii161d6knyqt5v` FOREIGN KEY (`idEstado`) REFERENCES `estados` (`idEstado`);

--
-- Filtros para la tabla `valoracion`
--
ALTER TABLE `valoracion`
  ADD CONSTRAINT `FK9ws5edcs6t6nb18ywne5dierf` FOREIGN KEY (`idEvento`) REFERENCES `evento` (`idEvento`),
  ADD CONSTRAINT `FKa4deqn8awu1ps0qkjsnjdwity` FOREIGN KEY (`idCliente`) REFERENCES `usuario` (`idUsuario`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
