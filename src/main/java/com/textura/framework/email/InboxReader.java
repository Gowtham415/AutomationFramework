package com.textura.framework.email;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import org.jsoup.Jsoup;

public class InboxReader {

	// Use Email class to verify emails.

	/**
	 * Adds an email configuration. This method sets the username to the email.
	 * @param email
	 * @param server
	 * @param protocol
	 * @param password
	 */
	public static void addUser(String email, String server, String protocol, String password) {
		emailSettingsMap.put(email, new EmailSettings(email, server, protocol, password));
	}

	/**
	 * Email can be different from username. This method accepts and saves both.
	 * @param email
	 * @param user
	 * @param server
	 * @param protocol
	 * @param password
	 */
	public static void addUser(String email, String user, String server, String protocol, String password) {
		emailSettingsMap.put(email, new EmailSettings(user, server, protocol, password));
	}

	private static class EmailSettings {

		public String user;
		public String server;
		public String protocol;
		public String password;

		public EmailSettings(String user, String server, String protocol, String password) {
			this.user = user;
			this.server = server;
			this.protocol = protocol;
			this.password = password;
		}

		public String toString() {
			return "{user: " + user + " server: " + server + " protocol: " + protocol + " password: " + password + "}";
		}
	}

	static Map<String, EmailSettings> emailSettingsMap = new HashMap<String, EmailSettings>();
	static {
		emailSettingsMap.put("pqmautomation1@texturacorp.com", new EmailSettings("pqmautomation1@texturacorp.com",
				"ch1prd0710.outlook.com", "imaps", "2tUdwuq7"));
		emailSettingsMap.put("pqmautomation2@texturacorp.com", new EmailSettings("pqmautomation2@texturacorp.com",
				"ch1prd0710.outlook.com", "imaps", "2tUdwuq7"));
		emailSettingsMap.put("pqmautomation3@texturacorp.com", new EmailSettings("pqmautomation3@texturacorp.com",
				"ch1prd0710.outlook.com", "imaps", "2tUdwuq7"));
		emailSettingsMap.put("qatest2@texturacorp.com", new EmailSettings("qatest2@texturacorp.com", "ch1prd0710.outlook.com", "imaps",
				"ZZLPdP0Y1"));
		emailSettingsMap.put("qatest3@texturacorp.com", new EmailSettings("qatest3@texturacorp.com", "ch1prd0710.outlook.com", "imaps",
				"ZZLPdP0Y1"));
		emailSettingsMap.put("qatest4@texturacorp.com", new EmailSettings("qatest4@texturacorp.com", "ch1prd0710.outlook.com", "imaps",
				"ZZLPdP0Y1"));
		emailSettingsMap.put("qatest5@texturacorp.com", new EmailSettings("qatest5@texturacorp.com", "ch1prd0710.outlook.com", "imaps",
				"ZZLPdP0Y1"));
		emailSettingsMap.put("qatest6@texturacorp.com", new EmailSettings("qatest6@texturacorp.com", "ch1prd0710.outlook.com", "imaps",
				"ZZLPdP0Y1"));
		
//		emailSettingsMap.put("cegbu-textura-qaauto06_us@oracle.com", new EmailSettings("cegbu-textura-qaauto06_us@oracle.com",
//				"beehiveonline.oracle.com", "imaps", "PQMTest1234!"));
		emailSettingsMap.put("cegbu-textura-qaauto01_us@oracle.com", new EmailSettings("cegbu-textura-qaauto01_us@oracle.com",
				"stbeehive.oracle.com", "imaps", "PQMTest1234!"));
		emailSettingsMap.put("cegbu-textura-qaauto02_us@oracle.com", new EmailSettings("cegbu-textura-qaauto02_us@oracle.com",
				"stbeehive.oracle.com", "imaps", "PQMTest1234!"));
		emailSettingsMap.put("cegbu-textura-qaauto03_us@oracle.com", new EmailSettings("cegbu-textura-qaauto03_us@oracle.com",
				"stbeehive.oracle.com", "imaps", "PQMTest1234!"));
		emailSettingsMap.put("cegbu-textura-qaauto04_us@oracle.com", new EmailSettings("cegbu-textura-qaauto04_us@oracle.com",
				"stbeehive.oracle.com", "imaps", "PQMTest1234!"));
		emailSettingsMap.put("cegbu-textura-qaauto05_us@oracle.com", new EmailSettings("cegbu-textura-qaauto05_us@oracle.com",
				"stbeehive.oracle.com", "imaps", "PQMTest1234!"));
		emailSettingsMap.put("cegbu-textura-qaauto06_us@oracle.com", new EmailSettings("cegbu-textura-qaauto06_us@oracle.com",
				"stbeehive.oracle.com", "imaps", "PQMTest1234!"));
	}

	protected static Map<String, javax.mail.Folder> openInboxes = new HashMap<String, javax.mail.Folder>();

	public static void deleteOldMessages(String email) {
		InboxReader i = new InboxReader(email);
		try {
			System.out.println("Deleting old messages from " + email);
			List<Msg> messages = i.getAllMessages();
			int k = 0;
			for (int j = 0; j < messages.size(); j++) {
				Date msgDate = messages.get(j).getDate();
				if (msgDate != null) {
					Calendar current = Calendar.getInstance();
					current.add(Calendar.MINUTE, -30);
					Calendar em = Calendar.getInstance();
					em.setTime(msgDate);
					if (em.compareTo(current) < 0) {
						messages.get(j).delete();
					}
				}
				// every 200, close the inbox to delete the marked messages
				if (k++ % 200 == 199) {
					k = 0;
					i = new InboxReader(email);
					messages = i.getAllMessages();
					j = -1;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		InboxReader.closeAll();
	}

	/**
	 * Fascade interface to interact with Messages
	 * 
	 */
	public interface Msg {

		public String getSubject();

		public String getBody();

		public String getHTML();
		
		public void markAsDeleted();

		public void delete();
		
		public List<String> getFrom();

		public Date getDate();
		
	}

	/**
	 * Class that creates a copy of the Message when instantiated. Retains values even if original message was deleted.
	 * 
	 */
	public static class MsgCopy implements Msg {

		private String subject;
		private String body;
		private List<String> from;
		private Date date;
		private String html;

		public MsgCopy(MsgImpl m) {
			this.subject = m.getSubject();
			this.body = m.getBody();
			this.html = m.getHTML();
			this.from = m.getFrom();
			this.date = m.getDate();
		}

		public String getSubject() {
			return subject;
		}

		public String getBody() {
			return body;
		}

		public void markAsDeleted() {
		}

		public void delete(){
			
		}
		
		public String getHTML() {
			return html;
		}

		public List<String> getFrom() {
			return from;
		}

		public Date getDate() {
			return date;
		}
	}

	/**
	 * Wrapper over the Message class. Does not proprogate exceptions.
	 * 
	 */
	public static class MsgImpl implements Msg {
		
		private javax.mail.Folder inbox;
		
		public MsgImpl(Message m, javax.mail.Folder inbox) {
			this.m = m;
			this.inbox = inbox;
		}

		public String getSubject() {
			try {
				return m.getSubject();
			} catch (Exception e) {
				return "null";
			}
		}

		public String getBody() {
			try {
				String ct = m.getContentType();
//				System.out.println("Trying to get the email content type...");
//				System.out.println("The content type is " + ct);
				if (ct.toLowerCase().contains("text/html")) {
					String html = m.getContent().toString();
					html = noTags(html);
//					System.out.println("No Tags HTML Content is the following: \n"+ html +"");
					return html;
				} else if (ct.contains("multipart")) {
					Multipart part = (Multipart) m.getContent();
					String text = "";
					for (int i = 0; i < part.getCount(); i++) {
						BodyPart b = part.getBodyPart(i);
						text += b.getContent().toString().replace("\r", "");
					}
//					System.out.println("No Tags HTML Content is the following: \n"+ text +"");
					return text;
				} else {
					return ct.toString().replace("\r", "");
				}
			} catch (Exception e) {
				return "null";
			}
		}

		public String getHTML() {
			try {
				if (m.getContentType().contains("text/html")) {
					String html = m.getContent().toString();
					return html;
				} else {
					return m.getContent().toString().replace("\r", "");
				}
			} catch (Exception e) {
				return "null";
			}
		}

		public List<String> getFrom() {
			List<String> from = new ArrayList<String>();
			try {
				for (Address a : m.getFrom()) {
					from.add(a.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return from;
		}

		public Date getDate() {
			try {
				return m.getSentDate();
			} catch (Exception e) {
				return null;
			}
		}
		
		//For this situation: Only mark this email message TO BE DELETED, 
		//	and then it will delete those TO-BE-DELETED later on after the invocation of inbox.close(true);
		public void markAsDeleted() {
			try {
				m.setFlag(Flags.Flag.DELETED, true);
			} catch (Exception e) {
			}
		}
		
		//For this situation: You need to delete this email message immediately
		public void delete() {
			try {
				m.setFlag(Flags.Flag.DELETED, true);
				inbox.close(true);
			} catch (Exception e) {
			}
		}

		private javax.mail.Message m;
	}

	private javax.mail.Folder inbox;

	public static synchronized InboxReader getInstance(String email) {
		return new InboxReader(email);
	}

	protected InboxReader(String email) {
		if (emailSettingsMap.get(email) == null) {
			throw new RuntimeException(
					"Configuration for email "
							+ email
							+ " could not be found. Ensure that InboxReader.adduser() was used to initialize email configuration.\nCurrent configuration:\n"
							+ emailSettingsMap);
		}
		if (openInboxes.get(email) != null) {
			inbox = openInboxes.get(email);
			try {
				inbox.close(true);
				inbox.open(javax.mail.Folder.READ_WRITE);
				if (inbox.isOpen()) {
					return;
				} else {
					openInboxes.remove(email);
				}
			} catch (Exception e) {
			}
		}
		Exception f = null;
		for (int i = 0; i < 3; i++) {
			try {
				Properties props = System.getProperties();
				props.setProperty("mail.store.protocol", "imaps");

				javax.mail.Session session = javax.mail.Session.getDefaultInstance(props, null);
				javax.mail.Store store = session.getStore(emailSettingsMap.get(email).protocol);
				String password = emailSettingsMap.get(email).password;
				store.connect(emailSettingsMap.get(email).server, emailSettingsMap.get(email).user, password);
				inbox = store.getFolder("Inbox");
				inbox.open(javax.mail.Folder.READ_WRITE);
				openInboxes.put(email, inbox);
				return;

			} catch (Exception e) {
				e.printStackTrace();
				f = e;
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		throw new RuntimeException("Failed connecting to email: " + email + " " + emailSettingsMap.get(email), f);
	}

	public List<Msg> getAllMessages() {
		
		List<Msg> messages = new ArrayList<Msg>();
		try {
			javax.mail.Message ms[] = inbox.getMessages();
			for (javax.mail.Message m : ms) {
				messages.add(new MsgImpl(m, inbox));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return messages;
	}
	
	public void deleteMessageContainsText(String subject, String bodyText) {
		double begin = System.currentTimeMillis() / 1000.0;
		double time = 0;
		int timeout = 120;
		while (time < timeout) {
			try {
				Message messages[] = inbox.getMessages();
				for(Message m: messages) {
					if (m.getSubject().trim().equals(subject.trim())) {
						try {
							String ct = m.getContentType();
							if (ct.toLowerCase().contains("text/html")) {
								String html = m.getContent().toString();
								html = noTags(html);
								System.out.println("No Tags HTML Content is the following: \n"+ html +"");
								if (html.contains(bodyText)) {
									m.setFlag(Flag.DELETED, true);
								}
								inbox.close(true);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			time = System.currentTimeMillis() / 1000.0 - begin;
		}
	}

	public void deleteAllMessages() {
		try {
			javax.mail.Message messages[] = inbox.getMessages();
			int i = 1;
			for (javax.mail.Message message : messages) {
				System.out.println(i++ + "/" + messages.length);
				message.setFlag(javax.mail.Flags.Flag.DELETED, true);
			}
			inbox.close(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static String noTags(String html) {
		String text = Jsoup.parse(html.replaceAll("(?i)<br[^>]*>", "br2nl").replaceAll("\n", "br2nl")).text();
		text = text.replaceAll("br2nl ", "\n").replaceAll("br2nl", "\n").trim();
		return text;
	}

	public static void closeAll() {
		for (String s : openInboxes.keySet()) {
			try {
				javax.mail.Folder inbox = openInboxes.get(s);
				inbox.close(true);

			} catch (Exception e) {
			}
		}
		openInboxes.clear();
	}

	public static void close(String email) {
		try {
			openInboxes.get(email).close(true);
			openInboxes.remove(email);
		} catch (NullPointerException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}