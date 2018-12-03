package com.carvajal.facturaclaro.bc;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import com.carvajal.facturaclaro.ral.EventsPackageQuery;
import com.carvajal.facturaclaro.ral.MD5GeneratorRAL;
import com.carvajal.facturaclaro.ral.RenameInvoiceFile;
import com.carvajal.facturaclaro.ral.StatusPackageQueryRAL;
import com.carvajal.facturaclaro.ral.StatusProcessingQueryRAL;
import com.carvajal.facturaclaro.ral.dto.ActivationDTO;
import com.carvajal.facturaclaro.ral.dto.AuthorizationDTO;
import com.carvajal.facturaclaro.ral.dto.LoginDTO;
import com.carvajal.facturaclaro.ral.dto.NotificationDTO;
import com.carvajal.facturaclaro.ral.dto.ResponseDTO;
import com.carvajal.facturaclaro.sl.ActivationSL;
import com.carvajal.facturaclaro.sl.LoginSL;
import com.carvajal.facturaclaro.sl.NotificationSL;
import com.carvajal.facturaclaro.utils.FilesSender;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

/**
 * <pre>
 * Fecha      Autor     
 * 21-11-2018 Diana Marcela Valencia
 * </pre>
 * 
 * Logica de negocio del envío de facturas para Claro - Carvajal
 * 
 * @author Diana Marcela Valencia
 * @version 1.0
 * @category Business Component
 **/

public class AuthorizationBC {

	public static ResponseDTO response = new ResponseDTO();

	/**
	 * <pre>
	 * Fecha      Autor     
	 * 21-11-2018 Diana Marcela Valencia
	 * </pre>
	 * 
	 * Se realiza la autorización de envío a procesamiento de una factura, NC o ND
	 * enviada al CENFINANCIERO
	 * 
	 * @author Diana Marcela Valencia
	 * @param aut Objeto AuthorizationDTO
	 * @return respuesta Objeto Login
	 * @throws IOException
	 * @throws SQLException
	 * @throws SftpException
	 * @throws JSchException
	 * 
	 **/

	public boolean isOkRetention(AuthorizationDTO aut) {
		ResponseDTO response = notificacionEnvioPaqueteRetencion(aut);
		if (response.getCodError().equalsIgnoreCase("200") && response.getCodErrorItem().equalsIgnoreCase("200")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isOkSendPackage(AuthorizationDTO aut) {

		ResponseDTO response = notificacionEnvioSinRetencion(aut);
		if (response.getCodError().equalsIgnoreCase("200") && response.getCodErrorItem().equalsIgnoreCase("200")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isOkFailPackage(AuthorizationDTO aut) {
		ResponseDTO response = notificacionEnvioFallidoSinRetencion(aut);
		if (response.getCodError().equalsIgnoreCase("200") && response.getCodErrorItem().equalsIgnoreCase("200")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isOkCancelledPackage(AuthorizationDTO aut) {
		ResponseDTO response = cancelarEnvioPaquete(aut);
		if (response.getCodError().equalsIgnoreCase("200") && response.getCodErrorItem().equalsIgnoreCase("200")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isOkAutomaticCancelledPackage(AuthorizationDTO aut) {
		ResponseDTO response = cancelacionAutEnvioPaquete(aut);
		if (response.getCodError().equalsIgnoreCase("200") && response.getCodErrorItem().equalsIgnoreCase("200")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isOkAuthorizationPackage(AuthorizationDTO aut) {
		ResponseDTO response = notificacionEnvioConRetencion(aut);
		if (response.getCodError().equalsIgnoreCase("200") && response.getCodErrorItem().equalsIgnoreCase("200")) {
			return true;
		} else {
			return false;
		}
	}

	public ResponseDTO notificacionEnvioPaqueteRetencion(AuthorizationDTO aut) {
		try {
			FilesSender fileSender = new FilesSender(aut.getConfiSftp());
			fileSender.sendFiles(aut);

			NotificationDTO notification = realizarNotificacion(aut);
			if (notification != null) {
				ArrayList<String> itemsStatus = new ArrayList<String>();

				StatusProcessingQueryRAL.statusProcessingQuery(aut);
				String statusProcessingPackage = StatusProcessingQueryRAL.statusProcessingPackage;

				if (statusProcessingPackage != null) {
					if (statusProcessingPackage.equalsIgnoreCase("OK")) {
						StatusPackageQueryRAL.statusPackage(aut);
						String status = StatusPackageQueryRAL.status;
						if (status.equalsIgnoreCase("STOPPED")) {
							response.setCodError("200");
							response.setMessage("El estado del paquete es: " + status);
							StatusPackageQueryRAL.statusPackageItems(aut);
							itemsStatus = StatusPackageQueryRAL.itemStatus;

							for (int i = 0; i < itemsStatus.size(); i++) {
								if (itemsStatus.get(i).equalsIgnoreCase("STOPPED")) {
									response.setCodErrorItem("200");
									response.setMessageItem(itemsStatus.get(i));
								} else {
									response.setCodErrorItem("404");
									response.setMessageItem("El estado del paquete es: " + itemsStatus.get(i));
								}
							}
						} else {
							response.setCodError("404");
							response.setMessage("El estado del paquete es: " + status);
						}
					} else {
						response.setCodError("404");
						response.setMessage(
								"El estado de procesamiento del paquete no es OK: " + statusProcessingPackage);
					}
				} else {
					response.setCodError("404");
					response.setMessage("El paquete no fue procesado");
				}
				System.out.println("Esperando actualización de eventos");
				this.waitQuery(300);
			} else {
				response.setCodError("405");
				response.setMessage("No se logro establecer la petición de notificación del paquete");
				System.out.println("No se logro establecer la petición de notificación del paquete");
			}
			return response;
		} catch (SQLException e) {
			System.out.println("El intento de conexión falló, revisar la conexión a la VPN");
		} catch (JSchException e) {
			System.out.println("No se logró establecer la conexión Sftp");
		} catch (SftpException e) {
			System.out.println("No se logró establecer la conexión Sftp");
		} catch (IOException e) {
			System.out.println("No se logró leer el archivo de entrada, verique la ruta del archivo");
		}
		return response;
	}

	public boolean validacionEventosRetencion(AuthorizationDTO aut) {
		try {
			EventsPackageQuery.eventsPackage(aut);
			ArrayList<String> eventsPackage = EventsPackageQuery.messageEvent;

			for (int i = 0; i < eventsPackage.size(); i++) {
				if (eventsPackage.get(i).contains("EVENT10")) {
					response.setCodErrorItem("200");
					response.setMessage("El paquete genero correctamente el EVENT10: " + eventsPackage.get(i));
				} else {
					response.setCodErrorItem("404");
					response.setMessageItem("No se genero el evento EVENT10 para envío de paquetes sin retención");
				}
			}

			for (int i = 0; i < eventsPackage.size(); i++) {
				if (eventsPackage.get(i).contains("EVENT20")) {
					response.setCodError("200");
					response.setMessage("El paquete genero correctamente el EVENT20: " + eventsPackage.get(i));
					break;
				} else {
					response.setCodError("404");
					response.setMessage("No se genero el evento EVENT20 para envío de paquetes sin retención");
				}
			}

			if (response.getCodError().equalsIgnoreCase("200")) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			System.out.println("El intento de conexión falló, revisar la conexión a la VPN");
			return false;
		}
	}

	public boolean validacionEventosSinRetencion(AuthorizationDTO aut) {

		try {
			EventsPackageQuery.eventsPackage(aut);
			ArrayList<String> eventsPackage = EventsPackageQuery.messageEvent;

			for (int i = 0; i < eventsPackage.size(); i++) {
				if (eventsPackage.get(i).contains("EVENT10")) {
					response.setCodError("200");
					response.setMessage("El paquete genero correctamente el EVENT10: " + eventsPackage.get(i));
					break;
				} else {
					response.setCodError("404");
					response.setMessageItem("No se genero el evento EVENT10 para envío de paquetes sin retención");
				}
			}

			for (int i = 0; i < eventsPackage.size(); i++) {
				if (eventsPackage.get(i).contains("EVENT40")) {
					response.setCodError("200");
					response.setMessage("El paquete genero correctamente el EVENT40: " + eventsPackage.get(i));
					break;
				} else {
					response.setCodError("404");
					response.setMessage("No se genero el evento EVENT40 para envío de paquetes sin retención");
				}
			}

			if (response.getCodError().equalsIgnoreCase("200")) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			System.out.println("El intento de conexión falló, revisar la conexión a la VPN");
			return false;
		}
	}

	public boolean validacionEventosSinRetencionFallido(AuthorizationDTO aut) {
		try {
			EventsPackageQuery.eventsPackage(aut);
			ArrayList<String> eventsPackage = EventsPackageQuery.messageEvent;

			for (int i = 0; i < eventsPackage.size(); i++) {
				if (eventsPackage.get(i).contains("EVENT10")) {
					response.setCodError("200");
					response.setMessage("El paquete genero correctamente el EVENT10: " + eventsPackage.get(i));
					break;
				} else {
					response.setCodError("404");
					response.setMessageItem("No se genero el evento EVENT10 para envío de paquetes sin retención");
				}
			}

			for (int i = 0; i < eventsPackage.size(); i++) {
				if (eventsPackage.get(i).contains("EVENT50")) {
					response.setCodError("200");
					response.setMessage("El paquete genero correctamente el EVENT50: " + eventsPackage.get(i));
					break;
				} else {
					response.setCodError("404");
					response.setMessage("No se genero el evento EVENT50 para envío de paquetes sin retención");
				}
			}

			if (response.getCodError().equalsIgnoreCase("200")) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			System.out.println("El intento de conexión falló, revisar la conexión a la VPN");
			return false;
		}
	}

	public boolean validacionEventosCancelacionPaquete(AuthorizationDTO aut) {

		try {
			EventsPackageQuery.eventsPackage(aut);
			ArrayList<String> eventsPackage = EventsPackageQuery.messageEvent;

			for (int i = 0; i < eventsPackage.size(); i++) {
				if (eventsPackage.get(i).contains("EVENT10")) {
					response.setCodError("200");
					response.setMessage("El paquete genero correctamente el EVENT10: " + eventsPackage.get(i));
					break;
				} else {
					response.setCodError("404");
					response.setMessageItem("No se genero el evento EVENT10 para envío de paquetes sin retención");
				}
			}

			for (int i = 0; i < eventsPackage.size(); i++) {
				if (eventsPackage.get(i).contains("EVENT30")) {
					response.setCodError("200");
					response.setMessage("El paquete genero correctamente el EVENT30: " + eventsPackage.get(i));
					break;
				} else {
					response.setCodError("404");
					response.setMessage("No se genero el evento EVENT30 para envío de paquetes sin retención");
				}
			}

			if (response.getCodError().equalsIgnoreCase("200")) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			System.out.println("El intento de conexión falló, revisar la conexión a la VPN");
			return false;
		}
	}

	public boolean alertaCufe(AuthorizationDTO aut) {
		try {
			EventsPackageQuery.eventsPackage(aut);
			ArrayList<String> eventsPackage = EventsPackageQuery.messageEvent;
			ArrayList<String> nameItemsPackage = StatusPackageQueryRAL.nameDocument;
			for (int j = 0; j < nameItemsPackage.size(); j++) {
				for (int i = 0; i < eventsPackage.size(); i++) {
					if (eventsPackage.get(i).contains(nameItemsPackage.get(j)) && eventsPackage.get(i).contains(
							"El CUFE de factura de venta enviado no cumple con el cálculo especificado por la DIAN")) {
						response.setCodError("200");
						response.setMessage("El mensaje generado es: " + eventsPackage.get(i));
						break;
					} else {
						response.setCodError("404");
						response.setMessage("No se genero mensaje de alerta sobre la validación de Cufe");
					}
				}
			}
			if (response.getCodError().equalsIgnoreCase("200")) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			System.out.println("El intento de conexión falló, revisar la conexión a la VPN");
		}
		return false;
	}

	public boolean renombramientoArchivos(AuthorizationDTO aut) {

		ArrayList<String> transationIdItemList = new ArrayList<String>();
		String proccesingIdItem = "";
		String xmlDocName = "";
		try {
			RenameInvoiceFile.docNameXML(aut);
			String nameFileGovernment = RenameInvoiceFile.docNameXML;
			RenameInvoiceFile.transationIdItems(aut);
			transationIdItemList = RenameInvoiceFile.transationIdItemList;

			if (!transationIdItemList.isEmpty()) {
				for (int i = 0; i < transationIdItemList.size(); i++) {
					RenameInvoiceFile.processingIdItem(transationIdItemList.get(i));
					proccesingIdItem = RenameInvoiceFile.proccesingIdItem;
					RenameInvoiceFile.xmlDocName(proccesingIdItem);
					xmlDocName = RenameInvoiceFile.xmlDocName;
					if (xmlDocName != null) {
						if (xmlDocName.matches("face_[f,c,n,d][0-9]{10,11}[0-9A-Fa-f]{10}")) {
							response.setCodErrorItem("200");
							response.setMessageItem("El nombre de los items del paquete corresponde a la estructura ");
						} else {
							response.setCodErrorItem("404");
							response.setMessageItem(
									"El nombre del documento no corresponse a la estructura: " + xmlDocName);
							return false;
						}
					} else {
						response.setCodErrorItem("404");
						response.setMessageItem("No se ha generado el archivo de gobierno");
					}
				}
			} else {
				response.setCodError("404");
				response.setMessage("No se ha realizado el proceso de descomprimir el paquete enviado");
			}

			if (response.getCodError().equalsIgnoreCase("200")) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			System.out.println("El intento de conexión falló, revisar la conexión a la VPN");
		}
		return false;
	}

	public ResponseDTO notificacionEnvioSinRetencion(AuthorizationDTO aut) {

		try {
			FilesSender fileSender = new FilesSender(aut.getConfiSftp());
			fileSender.sendFiles(aut);
			NotificationDTO notification = realizarNotificacion(aut);

			if (notification != null) {
				System.out.println("Esperando el procesamiento del paquete");
				this.waitQuery(80);
				ArrayList<String> itemsStatus = new ArrayList<String>();
				ArrayList<String> nameItems = new ArrayList<String>();
				StatusProcessingQueryRAL.statusProcessingQuery(aut);
				String statusProcessingPackage = StatusProcessingQueryRAL.statusProcessingPackage;

				if (statusProcessingPackage != null) {
					if (statusProcessingPackage.equalsIgnoreCase("OK")) {
						StatusPackageQueryRAL.statusPackage(aut);
						String status = StatusPackageQueryRAL.status;
						if (status.equalsIgnoreCase("COMPLETED")) {
							response.setCodError("200");
							response.setMessage("El estado del paquete es: " + status);
							StatusPackageQueryRAL.statusPackageItems(aut);
							itemsStatus = StatusPackageQueryRAL.itemStatus;
							nameItems = StatusPackageQueryRAL.nameDocument;

							for (int i = 0; i < itemsStatus.size(); i++) {
								if (itemsStatus.get(i).equalsIgnoreCase("PROCESSED")) {
									response.setCodErrorItem("200");
									response.setMessageItem(
											"El estado de los items enviados es correcto: " + itemsStatus.get(i));
								} else {
									response.setCodErrorItem("404");
									response.setMessageItem("/r/nEl estado del item " + nameItems.get(i) + " es: "
											+ itemsStatus.get(i));
									return response;
								}
							}
						} else {
							response.setCodError("404");
							response.setMessage("El estado del paquete es: " + status);
							return response;
						}
					} else {
						response.setCodError("404");
						response.setMessage(
								"El estado de procesamiento del paquete no es OK: " + statusProcessingPackage);
					}
				} else {
					response.setCodError("404");
					response.setMessage("El paquete no fue procesado");
				}
				this.waitQuery(300);
				System.out.println("Esperando actualización de eventos");
			} else {
				response.setCodError("405");
				response.setMessage("No se logró realizar la petición de notificación para el paquete");
				System.out.println("No se logro establecer la petición de notificación del paquete");
			}
			return response;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("El intento de conexión falló, revisar la conexión a la VPN");
		} catch (JSchException e) {
			System.out.println("No se logró establecer la conexión Sftp");
		} catch (SftpException e) {
			System.out.println("No se logró establecer la conexión Sftp");
		}
		return response;
	}

	public ResponseDTO notificacionEnvioFallidoSinRetencion(AuthorizationDTO aut) {

		try {
			FilesSender fileSender = new FilesSender(aut.getConfiSftp());
			fileSender.sendFiles(aut);
			NotificationDTO notification = realizarNotificacion(aut);

			if (notification != null) {
				System.out.println("Esperando el procesamiento del paquete");
				this.waitQuery(80);
				ArrayList<String> itemsStatus = new ArrayList<String>();
				ArrayList<String> nameItems = new ArrayList<String>();
				StatusProcessingQueryRAL.statusProcessingQuery(aut);
				String statusProcessingPackage = StatusProcessingQueryRAL.statusProcessingPackage;

				if (statusProcessingPackage != null) {
					if (statusProcessingPackage.equalsIgnoreCase("OK")) {
						StatusPackageQueryRAL.statusPackage(aut);
						String status = StatusPackageQueryRAL.status;
						if (status.equalsIgnoreCase("PROCESSING")) {
							response.setCodError("200");
							response.setMessage("El estado del paquete es: " + status);
							StatusPackageQueryRAL.statusPackageItems(aut);
							itemsStatus = StatusPackageQueryRAL.itemStatus;
							nameItems = StatusPackageQueryRAL.nameDocument;

							for (int i = 0; i < itemsStatus.size(); i++) {
								if (itemsStatus.get(i).equalsIgnoreCase("FAIL")) {
									response.setCodErrorItem("200");
									response.setMessageItem("El estado del paquete enviado es: " + itemsStatus.get(i));
								} else {
									response.setCodErrorItem("404");
									response.setMessageItem("/r/nEl estado del item " + nameItems.get(i) + " es: "
											+ itemsStatus.get(i));
									return response;
								}
							}
							return response;
						} else {
							response.setCodError("404");
							response.setMessage("El estado del paquete es: " + status);
							return response;
						}
					} else {
						response.setCodError("404");
						response.setMessage(
								"El estado de procesamiento del paquete no es OK: " + statusProcessingPackage);
						return response;
					}
				} else {
					response.setCodError("404");
					response.setMessage("El paquete no fue procesado");

				}
				System.out.println("Esperando actualización de eventos");
				this.waitQuery(300);
				return response;
			} else {
				response.setCodError("405");
				response.setMessage("No se logró realizar la petición de notificación para el paquete");
				System.out.println("No se logro establecer la petición de notificación del paquete");
				return response;
			}
		} catch (IOException e) {
			System.out.println("No se logró leer el archivo de entrada, verique la ruta del archivo");
		} catch (JSchException e) {
			System.out.println("No se logró establecer la conexión Sftp");
		} catch (SftpException e) {
			System.out.println("No se logró establecer la conexión Sftp");// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("El intento de conexión falló, revisar la conexión a la VPN");
		}
		return response;
	}

	private ResponseDTO cancelarEnvioPaquete(AuthorizationDTO aut) {

		try {
			FilesSender fileSender = new FilesSender(aut.getConfiSftp());
			fileSender.sendFiles(aut);
			NotificationDTO notification = realizarNotificacion(aut);

			if (notification != null) {
				System.out.println("Esperando el procesamiento del paquete");
				this.waitQuery(80);
				ActivationDTO activation = activacionDeEnvio(aut);
				if (activation != null) {
					ArrayList<String> itemsStatus = new ArrayList<String>();
					StatusProcessingQueryRAL.statusProcessingQuery(aut);
					String statusProcessingPackage = StatusProcessingQueryRAL.statusProcessingPackage;

					if (statusProcessingPackage.equalsIgnoreCase("OK")) {
						StatusPackageQueryRAL.statusPackage(aut);
						String status = StatusPackageQueryRAL.status;
						if (status.equalsIgnoreCase("CANCELLED")) {
							response.setCodError("200");
							response.setMessage("El estado del paquete es correcto: " + status);
							StatusPackageQueryRAL.statusPackageItems(aut);
							itemsStatus = StatusPackageQueryRAL.itemStatus;

							for (int i = 0; i < itemsStatus.size(); i++) {
								if (itemsStatus.get(i).equalsIgnoreCase("CANCELLED")) {
									response.setCodErrorItem("200");
									response.setMessageItem(itemsStatus.get(i));
								} else {
									response.setCodErrorItem("404");
									response.setMessageItem(
											"El estado del paquete es incorrecto: " + itemsStatus.get(i));
									return response;
								}
							}
							return response;
						} else {
							response.setCodError("404");
							response.setMessage("El estado del paquete enviado es: " + status);
							return response;
						}
					} else {
						response.setCodError("404");
						response.setMessage(
								"El paquete enviado para el flujo de retención cancelación no ha sido procesado");

					}
					System.out.println("Esperando actualización de eventos");
					this.waitQuery(300);
					return response;
				} else {
					response.setCodError("405");
					response.setMessage("No se logró realizar la petición de activación para el paquete");
					System.out.println("No se logro establecer la petición de activación del paquete");
					return response;
				}
			} else {
				response.setCodError("405");
				response.setMessage("No se logró realizar la petición de notificación para el paquete");
				System.out.println("No se logro establecer la petición de notificación del paquete");
				return response;
			}
		} catch (IOException e) {
			System.out.println("No se logró leer el archivo de entrada, verique la ruta del archivo");
		} catch (JSchException e) {
			System.out.println("No se logró establecer la conexión Sftp");
		} catch (SftpException e) {
			System.out.println("No se logró establecer la conexión Sftp");
		} catch (SQLException e) {
			System.out.println("El intento de conexión falló, revisar la conexión a la VPN");
		}
		return response;
	}

	private ResponseDTO cancelacionAutEnvioPaquete(AuthorizationDTO aut) {

		try {
			FilesSender fileSender = new FilesSender(aut.getConfiSftp());
			fileSender.sendFiles(aut);
			NotificationDTO notification = realizarNotificacion(aut);

			if (notification != null) {
				System.out.println("Esperando la cancelación automática del paquete.");
				this.waitQuery(300);
				ArrayList<String> itemsStatus = new ArrayList<String>();
				StatusProcessingQueryRAL.statusProcessingQuery(aut);
				String statusProcessingPackage = StatusProcessingQueryRAL.statusProcessingPackage;

				if (statusProcessingPackage.equalsIgnoreCase("OK")) {
					StatusPackageQueryRAL.statusPackage(aut);
					String status = StatusPackageQueryRAL.status;
					if (status.equalsIgnoreCase("CANCELLED")) {
						response.setCodError("200");
						response.setMessage("El estado del paquete es correcto: " + status);
						StatusPackageQueryRAL.statusPackageItems(aut);
						itemsStatus = StatusPackageQueryRAL.itemStatus;

						for (int i = 0; i < itemsStatus.size(); i++) {
							if (itemsStatus.get(i).equalsIgnoreCase("CANCELLED")) {
								response.setCodErrorItem("200");
								response.setMessageItem(itemsStatus.get(i));
							} else {
								response.setCodErrorItem("404");
								response.setMessageItem("El estado del paquete es incorrecto: " + itemsStatus.get(i));
								return response;
							}
						}
						return response;
					} else {
						response.setCodError("404");
						response.setMessage("El estado del paquete enviado es: " + status);
						return response;
					}
				} else {
					response.setCodError("404");
					response.setMessage(
							"El paquete enviado para el flujo de retención cancelación no ha sido procesado");
				}
				System.out.println("Esperando actualización de eventos");
				this.waitQuery(300);
				return response;
			} else {
				response.setCodError("405");
				response.setMessage("No se logró realizar la petición de notificación para el paquete");
				System.out.println("No se logró realizar la petición de notificación para el paquete");
				return response;
			}
		} catch (IOException e) {
			System.out.println("No se logró leer el archivo de entrada, verique la ruta del archivo");
		} catch (JSchException e) {
			System.out.println("No se logró establecer la conexión Sftp");
		} catch (SftpException e) {
			System.out.println("No se logró establecer la conexión Sftp");
		} catch (SQLException e) {
			System.out.println("El intento de conexión falló, revisar la conexión a la VPN");
		}
		return response;
	}

	public ResponseDTO notificacionEnvioConRetencion(AuthorizationDTO aut) {

		try {
			FilesSender fileSender = new FilesSender(aut.getConfiSftp());
			fileSender.sendFiles(aut);

			NotificationDTO notificacion = realizarNotificacion(aut);
			if (notificacion != null) {
				System.out.println("Esperando el procesamiento del paquete");
				this.waitQuery(80);
				ActivationDTO activation = activacionDeEnvio(aut);
				if (activation != null) {
					System.out.println("Esperando el procesamiento del paquete");
					this.waitQuery(60);
					ArrayList<String> itemsStatus = new ArrayList<String>();
					ArrayList<String> nameItems = new ArrayList<String>();
					StatusProcessingQueryRAL.statusProcessingQuery(aut);
					String statusProcessingPackage = StatusProcessingQueryRAL.statusProcessingPackage;

					if (statusProcessingPackage.equalsIgnoreCase("OK")) {
						StatusPackageQueryRAL.statusPackage(aut);
						String status = StatusPackageQueryRAL.status;
						if (status.equalsIgnoreCase("COMPLETED")) {
							response.setCodError("200");
							response.setMessage("El estado del paquete es correcto: " + status);
							StatusPackageQueryRAL.statusPackageItems(aut);
							itemsStatus = StatusPackageQueryRAL.itemStatus;
							nameItems = StatusPackageQueryRAL.nameDocument;

							for (int i = 0; i < itemsStatus.size(); i++) {
								if (itemsStatus.get(i).equalsIgnoreCase("PROCESSED")) {
									response.setCodErrorItem("200");
									response.setMessageItem(
											"El estado de los items enviados es correcto: " + itemsStatus.get(i));
								} else {
									response.setCodErrorItem("404");
									response.setMessageItem("/r/nEl estado del item " + nameItems.get(i) + " es: "
											+ itemsStatus.get(i));
									return response;
								}
							}
							return response;
						} else {
							response.setCodError("404");
							response.setMessage("El estado del paquete no es correcto: " + status);
							return response;
						}
					} else {
						response.setCodError("404");
						response.setMessage("El paquete enviado no ha sido procesado");
						return response;
					}
				} else {
					response.setCodError("405");
					response.setMessage("No se logró realizar la petición de activación para el paquete");
					System.out.println("No se logró realizar la petición de activación para el paquete");
					return response;
				}
			} else {
				response.setCodError("405");
				response.setMessage("No se logró realizar la petición de notificación para el paquete");
				System.out.println("No se logró realizar la petición de notificación para el paquete");
				return response;
			}
		} catch (IOException e) {
			System.out.println("No se logró leer el archivo de entrada, verique la ruta del archivo");
		} catch (SQLException e) {
			System.out.println("El intento de conexión falló, revisar la conexión a la VPN");
		} catch (SftpException e) {
			System.out.println("No se logró establecer la conexión Sftp");
		} catch (JSchException e) {
			System.out.println("No se logró establecer la conexión Sftp");
		}
		return null;
	}

	private LoginDTO obtenerToken(AuthorizationDTO aut) {

		try {
			Random aleatorio = new Random(System.currentTimeMillis());
			int intAletorio = aleatorio.nextInt(3000);
			aleatorio.setSeed(System.currentTimeMillis());

			String bodyWS = "{\"user\":\"" + aut.getLogin().getUser() + "\",\"password\":\""
					+ aut.getLogin().getPassword() + "\"}";
			String length = String.valueOf(bodyWS.length());
			String md5 = MD5GeneratorRAL.ContentMD5Base64(bodyWS, "MD5").trim();
			String date = returnDate();
			String transationId = "UUID-" + intAletorio;

			System.out.println("Realizando petición de login con los siguientes datos: fecha " + date + " length "
					+ length + " transationId " + transationId + " md5 " + md5);

			return LoginSL.getLogin(bodyWS, md5, date, transationId, length);
		} catch (IOException e) {
			System.out.println("No se logró leer el archivo de entrada, verique la ruta del archivo");
			return null;
		}
	}

	public NotificationDTO realizarNotificacion(AuthorizationDTO aut) {
		try {
			Random aleatorio = new Random(System.currentTimeMillis());
			int intAletorio = aleatorio.nextInt(3000);
			aleatorio.setSeed(System.currentTimeMillis());
			LoginDTO login = obtenerToken(aut);
			if (login != null) {
				String token = obtenerToken(aut).getToken();
				if (token != null) {
					String bodyWS = "{\"companyId\":\"" + aut.getNotificacion().getCompanyId() + "\",\"account\":\""
							+ aut.getNotificacion().getAccount() + "\",\"batchId\":\""
							+ aut.getNotificacion().getBatchId() + "\",\"packagesPaths\":[\""
							+ aut.getNotificacion().getPackagesPaths() + "\"]}";
					String length = String.valueOf(bodyWS);
					String md5 = MD5GeneratorRAL.ContentMD5Base64(bodyWS, "MD5").trim();
					String transationId = "UUID-" + intAletorio;
					String date = returnDate();

					System.out.println(
							"Realizando petición de notificación con los siguientes datos: fecha " + date + " length "
									+ length + " transationId " + transationId + " md5 " + md5 + " token " + token);
					return NotificationSL.getNotification(bodyWS, token, md5, date, transationId, length);
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (IOException e) {
			System.out.println("No se logró leer el archivo de entrada, verique la ruta del archivo");
			return null;
		}
	}

	private ActivationDTO activacionDeEnvio(AuthorizationDTO aut) {

		Random aleatorio = new Random(System.currentTimeMillis());
		int intAletorio = aleatorio.nextInt(3000);
		aleatorio.setSeed(System.currentTimeMillis());

		String token = obtenerToken(aut).getToken();
		ActivationDTO activation = aut.getActivation();
		if (activation != null) {
			String bodyWS = "{\"package\":\"" + aut.getActivation().getPackagesName() + "\",\"companyId\":\""
					+ aut.getActivation().getCompanyId() + "\",\"action\":\"" + aut.getActivation().getAction()
					+ "\",\"batchId\":\"" + aut.getActivation().getBatchId() + "\"}";
			String length = String.valueOf(bodyWS);
			String md5 = MD5GeneratorRAL.ContentMD5Base64(bodyWS, "MD5").trim();
			String date = returnDate();
			String transationId = "UUID-" + intAletorio;
			System.out.println("Realizando petición de Activación con los siguientes datos: fecha " + date + " length "
					+ length + " transationId " + transationId + " md5 " + md5 + " token " + token);
			return ActivationSL.getActivation(bodyWS, token, md5, date, transationId, length);
		} else {
			return null;
		}
	}

	private String returnDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
		calendar.add(Calendar.HOUR, 5);
		Date systemDate = calendar.getTime();
		String date = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH).format(systemDate);
		return date;

	}

	private void waitQuery(int segundos) {
		try {
			Thread.sleep(segundos * 1000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}
}