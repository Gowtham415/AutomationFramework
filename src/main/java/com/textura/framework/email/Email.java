package com.textura.framework.email;


import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import com.textura.framework.email.InboxReader.Msg;
import com.textura.framework.email.InboxReader.MsgImpl;
import com.textura.framework.environment.Project;
import com.textura.framework.objects.main.Page;

public class Email {

	public static int timeout = 300;
	protected WebDriver driver;

	public Email(WebDriver driver) {
		this.driver = driver;
	}

	protected static String parseInviteURLFromHTML(String s) {
		return Jsoup.parse(s).getElementsByTag("a").get(0).attr("href");
	}

	public static Email getConnection(WebDriver driver) {
		return new Email(driver);
	}
	
	/**
	 * Get the email(Msg) with specific subject matched
	 */
	public String searchEmail(String email, String subject, int timeout) {
		double begin = System.currentTimeMillis() / 1000.0;
		double time = 0;
		logToFile("Searching email " + email);
		while (time < timeout) {
			InboxReader inbox = InboxReader.getInstance(email);
			List<Msg> a = inbox.getAllMessages();
			try {
				new Actions(driver).moveByOffset(1, 1).build().perform();
				new Actions(driver).moveByOffset(-1, -1).build().perform();
			} catch (Exception e) {
				e.printStackTrace();
			}
			int i = 1;
			for (Msg m : a) {
				Page.printEmailFormattedMessage("Checking the "+ i +"th email in inbox...");
				if (m.getSubject().trim().equals(subject.trim())) {
					String body = m.getBody();
					return body;
				}
				// if email is 30 minutes or older, delete it
				Date msgDate = m.getDate();
//				System.out.println("Checking the "+ i +"th email: timestamp is " + msgDate.getTime());
//				System.out.println(" ");
				if (msgDate != null) {
					Calendar current = Calendar.getInstance();
					current.add(Calendar.MINUTE, -3);
					Calendar em = Calendar.getInstance();
					em.setTime(msgDate);
					if (em.compareTo(current) < 0) {
						Page.printEmailFormattedMessage("Deleting the "+ i +"th mail that existing for more than 30 minutes...");
						m.delete();
					}
				}
				i++;
			}
			time = System.currentTimeMillis() / 1000.0 - begin;
		}
		throw new RuntimeException("Email with subject: "+ subject +" Not Found");
	}
	
	public void deleteEmailContainsBody(String email, String subject, String body, String date) {
		int timeout = 150;
		double begin = System.currentTimeMillis() / 1000.0;
		double time = 0;
		logToFile("Searching email " + email);
		while (time < timeout) {
			InboxReader inbox = InboxReader.getInstance(email);
			List<Msg> a = inbox.getAllMessages();
			try {
				new Actions(driver).moveByOffset(1, 1).build().perform();
				new Actions(driver).moveByOffset(-1, -1).build().perform();
			} catch (Exception e) {
				e.printStackTrace();
			}
			int i = 1;
			for (Msg m : a) {
				Page.printEmailFormattedMessage("Checking the "+ i +"th email in inbox...");
				if (m.getSubject().trim().equals(subject.trim())) {
					if(m.getBody().contains(date)) {
						m.markAsDeleted();
					}
				}
				i++;
			}
			time = System.currentTimeMillis() / 1000.0 - begin;
		}
	}
	
	public void deleteEmailContainsBody(String email, String subject, String body) {
		int timeout = 150;
		double begin = System.currentTimeMillis() / 1000.0;
		double time = 0;
		logToFile("Searching email " + email);
		while (time < timeout) {
			InboxReader inbox = InboxReader.getInstance(email);
			List<Msg> a = inbox.getAllMessages();
			int i = 1;
			for (Msg m : a) {
				Page.printEmailFormattedMessage("Checking the "+ i +"th email in inbox...");
				if (m.getSubject().trim().equals(subject.trim())) {
					if(m.getBody().contains(body)) {
						m.markAsDeleted();
					}
				}
				i++;
			}
			time = System.currentTimeMillis() / 1000.0 - begin;
		}
	}
	
	public void deleteEmailContainsBody(String email, String subject, List<String> body) {
		int timeout = 150;
		double begin = System.currentTimeMillis() / 1000.0;
		double time = 0;
		logToFile("Searching email " + email);
		while (time < timeout) {
			InboxReader inbox = InboxReader.getInstance(email);
			List<Msg> a = inbox.getAllMessages();
			int i = 1;
			for (Msg m : a) {
				Page.printEmailFormattedMessage("Checking the "+ i +"th email in inbox...");
				if (m.getSubject().trim().equals(subject.trim())) {
					for (int j = 0; j < body.size(); j++) {
						if(m.getBody().contains(body.get(j))) {
							m.markAsDeleted();
						}
					}
				}
				i++;
			}
			time = System.currentTimeMillis() / 1000.0 - begin;
		}
	}
	
	public void deleteEmailsContainsSubject(String email, String subject) {
		int timeout = 150;
		double begin = System.currentTimeMillis() / 1000.0;
		double time = 0;
		logToFile("Searching email " + email);
		while (time < timeout) {
			InboxReader inbox = InboxReader.getInstance(email);
			List<Msg> a = inbox.getAllMessages();
			int i = 1;
			for (Msg m : a) {
				Page.printEmailFormattedMessage("Checking the "+ i +"th email in inbox...");
				if (m.getSubject().trim().contains(subject.trim())) {
						m.markAsDeleted();
				}
				i++;
			}
			time = System.currentTimeMillis() / 1000.0 - begin;
		}
	}

	/**
	 * Loops through the emails executing tester.matchMessage(Msg) on each
	 * Message
	 * 
	 * @param tester
	 *            an anonymous object which implements if message was found or not
	 * @return the first matched message
	 * @throws an
	 *             exception given by MsgTester.throwIfNotFound() if
	 *             MsgTester.throwIfNotFound() is not null
	 */
	public Msg searchEmail(String email, MsgTester tester, boolean checkHTML, int timeout) {
		double begin = System.currentTimeMillis() / 1000.0;
		double time = 0;
		logToFile("Searching email " + email);
		while (time < timeout) {
			InboxReader inbox = InboxReader.getInstance(email);
			List<Msg> a = inbox.getAllMessages();
			try {
				new Actions(driver).moveByOffset(1, 1).build().perform();
				new Actions(driver).moveByOffset(-1, -1).build().perform();
			} catch (Exception e) {
				e.printStackTrace();
			}
			int i = 1;
			for (Msg m : a) {
//				System.out.println("	[" + testCaseName + "] Checking the "+ i +"th email in inbox...");
				Page.printEmailFormattedMessage("Checking the "+ i +"th email in inbox...");
				if (checkHTML) {
					logToFile(m.getDate() + "\n" + m.getSubject() + "\n" + m.getHTML() + "\n"
							+ "-------------------------------------------------------------------------------------\n");
				} else {
					logToFile(m.getDate() + "\n" + m.getSubject() + "\n" + m.getBody() + "\n"
							+ "-------------------------------------------------------------------------------------\n");
				}
				if (tester.matchMessage(m)) {
					return m;
				}
				// if email is 30 minutes or older, delete it
				Date msgDate = m.getDate();
				if (msgDate != null) {
//					System.out.println("Checking the "+ i +"th email: timestamp is " + msgDate.getTime());
//					System.out.println(" ");
					Calendar current = Calendar.getInstance();
					current.add(Calendar.MINUTE, -10);
					Calendar em = Calendar.getInstance();
					em.setTime(msgDate);
					if (em.compareTo(current) < 0) {
//						System.out.println("	[" + testCaseName + "] Deleting the "+ i +"th mail that existing for more than 10 minutes...");
						Page.printEmailFormattedMessage("Deleting the "+ i +"th mail that existing for more than 10 minutes...");
						m.markAsDeleted();	//deleteNew
					}
				}
				i++;
			}
			time = System.currentTimeMillis() / 1000.0 - begin;
		}
		// if an exception should not be thrown upon not finding email,
		// return null for throwIfNotFound
		if (tester.throwIfNotFound() == null) {
			return null;
		}
		throw tester.throwIfNotFound();
	}

	public Msg searchEmail(String email, MsgTester tester) {
		return searchEmail(email, tester, false, timeout);
	}
	
	public Msg searchEmail(String email, MsgTester tester, int timeout) {
		return searchEmail(email, tester, false, timeout);
	}

	public Msg searchEmail(String email, MsgTester tester, boolean html) {
		return searchEmail(email, tester, html, timeout);
	}

	public interface MsgTester {

		/**
		 * Test a given message if it matches the one the test is looking for
		 * 
		 * @param m
		 * @return true if message is found, false if not the message you are
		 *         looking for
		 */
		public boolean matchMessage(Msg m);

		/**
		 * If no message is matched, a specified exception will be thrown if
		 * null is returned, no exception is thrown
		 * 
		 * @return an Exception to throw
		 */
		public RuntimeException throwIfNotFound();
	}

	/**
	 * Searches email for a given message
	 * 
	 * @param subject
	 * @param body
	 * @return true if found, false if not
	 */
	public boolean verifyEmail(String email, final String subject, final String body, final boolean delete) {
		logToFile("Searching for email:\n" + email + "\n\nSubject:\n" + subject + "\n\nBody:\n" + body + "\n\n");
		Msg a = searchEmail(email, new MsgTester() {
			@Override
			public boolean matchMessage(Msg m) {
				String msgBody = m.getBody();
				logToFile(m.getDate() + "\n" + m.getSubject() + "\n" + m.getBody() + "\n" + m.getHTML() + "\n"
						+ "-------------------------------------------------------------------------------------\n");
				if (m.getSubject().trim().equals(subject.trim())) {
					if (msgBody.trim().equals(body.trim())) {
						if (delete) {
							m.delete();
						}
						return true;
					} else {
						logToFile("Matched Subject: " + subject + " but not body");
						logToFile("Email Body:\n" + msgBody);
						logToFile("Expected Body:\n" + body);
						logToFile("Character comparison\nexpectedBody emailBody");
						for (int i = 0; i < Math.min(body.length(), msgBody.length()); i++) {
							if (body.charAt(i) != msgBody.charAt(i)) {
								logToFile("diff: " + literal(body.charAt(i)) + " " + literal(msgBody.charAt(i)));
								break;
							} else {
								logToFile(literal(body.charAt(i)) + " " + literal(msgBody.charAt(i)));
							}
						}
					}
				}
				return false;
			}

			@Override
			public RuntimeException throwIfNotFound() {
				return null;
			}
		});
		if (a == null) {
			return false;
		}
		return true;
	}
	
	public boolean verifyEmail(String email, final String subject, final String body, final boolean delete, int timeout) {
		logToFile("Searching for email:\n" + email + "\n\nSubject:\n" + subject + "\n\nBody:\n" + body + "\n\n");
		Msg a = searchEmail(email, new MsgTester() {
			@Override
			public boolean matchMessage(Msg m) {
				String msgBody = m.getBody();
				logToFile(m.getDate() + "\n" + m.getSubject() + "\n" + m.getBody() + "\n" + m.getHTML() + "\n"
						+ "-------------------------------------------------------------------------------------\n");
				if (m.getSubject().trim().equals(subject.trim())) {
					if (msgBody.trim().equals(body.trim())) {
						if (delete) {
//							m.markAsDeleted();
							m.delete();
						}
						return true;
					} else {
						logToFile("Matched Subject: " + subject + " but not body");
						logToFile("Email Body:\n" + msgBody);
						logToFile("Expected Body:\n" + body);
						logToFile("Character comparison\nexpectedBody emailBody");
						for (int i = 0; i < Math.min(body.length(), msgBody.length()); i++) {
							if (body.charAt(i) != msgBody.charAt(i)) {
								logToFile("diff: " + literal(body.charAt(i)) + " " + literal(msgBody.charAt(i)));
								break;
							} else {
								logToFile(literal(body.charAt(i)) + " " + literal(msgBody.charAt(i)));
							}
						}
					}
				}
				return false;
			}

			@Override
			public RuntimeException throwIfNotFound() {
				return null;
			}
		}, timeout);
		if (a == null) {
			return false;
		}
		return true;
	}

	public boolean verifyEmail(String email, final String subject, final String body) {
		return verifyEmail(email, subject, body, false);		/**false**/
	}
	
	/**For Cases that shouldn't receive any emails */
	public boolean verifyEmailWithTimeout(String email, final String subject, final String body, int timeout) {
		return verifyEmail(email, subject, body, false, timeout);
	}
	
	public boolean verifyEmailWithTimeoutAndDelete(String email, final String subject, final String body) {
		return verifyEmail(email, subject, body, true, 300);
	}
	
	public boolean verifyEmailWithTimeoutAndDelete(String email, final String subject, final String body, int timeout) {
		return verifyEmail(email, subject, body, true, timeout);
	}

	/**
	 * Turns multiple new lines into one new line, and multiple spaces into one space
	 * 
	 * @param text
	 * @return
	 */
	public static String normalizeString(String text) {
		return text.replaceAll("[\n]+", "newLine").replaceAll("[\\s]+", " ").replace("newLine", "\n").replace(" \n", "\n").replace("\n ",
				"\n");
	}

	public static String normalizeStringNew(String text) {
		return text.replaceAll("[\n]+", "newLine").replaceAll("[\\s]+", " ").replace("newLine", "\n").replace(" \n", "\n").replace("\n ",
				"\n").replace("\n\n", "\n").replace("\n\n ", "\n").replace(" \n\n", "\n");
	}
	
	public boolean verifyEmailNormalizeNew(String email, final String subject, final String bodyArg) {
		final String body = normalizeStringNew(bodyArg);
		Msg a = searchEmail(email, new MsgTester() {

			@Override
			public boolean matchMessage(Msg m) {
				String msgBody = m.getBody();
				msgBody = normalizeStringNew(msgBody);
				logToFile(m.getDate() + "\n" + m.getSubject() + "\n" + m.getBody() + "\n"
						+ "-------------------------------------------------------------------------------------\n");
				if (m.getSubject().trim().equals(subject.trim())) {
					if (msgBody.trim().equals(body.trim())) {
						return true;
					} else {
						logToFile("Matched Subject: " + subject + " but not body");
						logToFile("Email Body:\n" + msgBody);
						logToFile("Expected Body:\n" + body);
						logToFile("Character comparison\nexpectedBody emailBody");
						for (int i = 0; i < Math.min(body.length(), msgBody.length()); i++) {
							if (body.charAt(i) != msgBody.charAt(i)) {
								logToFile("diff: " + literal(body.charAt(i)) + " " + literal(msgBody.charAt(i)));
								break;
							} else {
								logToFile(literal(body.charAt(i)) + " " + literal(msgBody.charAt(i)));
							}
						}
					}
				}
				return false;
			}

			@Override
			public RuntimeException throwIfNotFound() {
				return null;
			}
		});
		if (a == null) {
			return false;
		}
		return true;
	}
	
	public boolean verifyEmailNormalize(String email, final String subject, final String bodyArg) {
		final String body = normalizeString(bodyArg);
		Msg a = searchEmail(email, new MsgTester() {

			@Override
			public boolean matchMessage(Msg m) {
				String msgBody = m.getBody();
				msgBody = normalizeString(msgBody);
				logToFile(m.getDate() + "\n" + m.getSubject() + "\n" + m.getBody() + "\n"
						+ "-------------------------------------------------------------------------------------\n");
				if (m.getSubject().trim().equals(subject.trim())) {
					if (msgBody.trim().equals(body.trim())) {
						m.delete();
						return true;
					} else {
						logToFile("Matched Subject: " + subject + " but not body");
						logToFile("Email Body:\n" + msgBody);
						logToFile("Expected Body:\n" + body);
						logToFile("Character comparison\nexpectedBody emailBody");
						for (int i = 0; i < Math.min(body.length(), msgBody.length()); i++) {
							if (body.charAt(i) != msgBody.charAt(i)) {
								logToFile("diff: " + literal(body.charAt(i)) + " " + literal(msgBody.charAt(i)));
								break;
							} else {
								logToFile(literal(body.charAt(i)) + " " + literal(msgBody.charAt(i)));
							}
						}
					}
				}
				return false;
			}

			@Override
			public RuntimeException throwIfNotFound() {
				return null;
			}
		});
		if (a == null) {
			return false;
		}
		return true;
	}

	public static String literal(char c) {
		if (c == '\n') {
			return "\\n";
		}
		if (c == ' ') {
			return "space";
		}
		if (c == '\r') {
			return "\\r";
		}
		return "" + c;
	}

	/**
	 * Searches email for a given message
	 * 
	 * @param subject
	 * @return true if found, false if not
	 */
	public boolean verifyEmail(String email, final String subject, int timeout) {
		Msg a = searchEmail(email, new MsgTester() {

			@Override
			public boolean matchMessage(Msg m) {
				if (m.getSubject().trim().equals(subject.trim())) {
					return true;
				}
				return false;
			}

			@Override
			public RuntimeException throwIfNotFound() {
				return null;
			}
		}, false, timeout);
		if (a == null) {
			return false;
		}
		return true;
	}
	
	public boolean verifyEmail(String email, final String subject) {
		return verifyEmail(email, subject, timeout);
	}
	
	//Not gonna be working because there would be some other emails with same subject but old than 10min, then will cause Null Pointer exception
	public boolean verifyEmailContains(String email, final String subject) {
		Msg a = searchEmail(email, new MsgTester() {

			@Override
			public boolean matchMessage(Msg m) {
				if (m.getSubject().trim().contains(subject.trim())) {
					return true;
				}
				return false;
			}

			@Override
			public RuntimeException throwIfNotFound() {
				return null;
			}
		}, false, timeout);
		if (a == null) {
			return false;
		}
		return true;
	}

	public boolean verifyEmailContainsAndDeleteWithTimeOut(String email, final String subject, int timeout) {
		Msg a = searchEmail(email, new MsgTester() {

			@Override
			public boolean matchMessage(Msg m) {
				if (m.getSubject().trim().contains(subject.trim())) {
					m.markAsDeleted();
					return true;
				}
				return false;
			}

			@Override
			public RuntimeException throwIfNotFound() {
				return null;
			}
		}, false, timeout);
		InboxReader.close(email);
		if (a == null) {
			return false;
		}
		return true;
	}
	
	public boolean verifyEmailContainsBodyAndDelete(String email, final String subject, final List<String> containingContents) {
		return verifyEmailContainsBodyAndDelete(email, subject, timeout, containingContents);
	}
	
	/**Verify matching pattern is that the subjects are the same or email subject contains pattern subject, the bodys contain specific word**/
	public boolean verifyEmailContainsBodyAndDelete(String email, final String subject, int timeout, final List<String> containingContents) {
		Msg a = searchEmail(email, new MsgTester() {

			@Override
			public boolean matchMessage(Msg m) {
				if (m.getSubject().trim().contains(subject.trim())) {
					for (String s : containingContents) {
						if (!m.getBody().trim().contains(s)) {	
//							System.out.println("   '" + s + "' is not found in the email body!!");
							Page.printEmailFormattedMessage("   '" + s + "' is not found in the email body!!");
							return false;
						}
					}
					m.markAsDeleted();
					return true;
				}
				return false;
			}

			@Override
			public RuntimeException throwIfNotFound() {
				return null;
			}
		}, false, timeout);
		InboxReader.close(email);
		if (a == null) {
			return false;
		}
		return true;
	}
	
	public boolean verifyEmailContainsAndDelete(String email, final String subject) {
		return verifyEmailContainsAndDeleteWithTimeOut(email, subject, timeout);
	}
	
	public boolean verifyEmailContainsAndDelete(String email, final String subject, final List<String> excludedSubjectParts) {
		return verifyEmailContainsAndDelete(email, subject, timeout, excludedSubjectParts);
	}

	public boolean verifyEmailContainsAndDelete(String email, final String subject, int timeout, final List<String> excludedSubjectParts) {
		Msg a = searchEmail(email, new MsgTester() {

			@Override
			public boolean matchMessage(Msg m) {
				if (m.getSubject().trim().contains(subject.trim())) {
					for (String s : excludedSubjectParts) {
						if (m.getSubject().trim().contains(s)) {
							return false;
						}
					}
					m.markAsDeleted();
					return true;
				}
				return false;
			}

			@Override
			public RuntimeException throwIfNotFound() {
				return null;
			}
		}, false, timeout);
		InboxReader.close(email);
		if (a == null) {
			return false;
		}
		return true;
	}

	/**
	 * Searches email for a given message
	 * 
	 */
	public boolean verifyEmailWithWildCard(String email, final String subject, final String body) {
		logToFile(subject + "\n body!!!\n" + body+ "\n...:");
		Msg a = searchEmail(email, new MsgTester() {

			@Override
			public boolean matchMessage(Msg m) {
				String msgBody = m.getBody();
				if (m.getSubject().trim().equals(subject.trim())) {
					if (wildCardMatchWithoutAsterisk(msgBody, body)) {
						m.delete();
						return true;
					} else {

					}
				}
				return false;
			}

			@Override
			public RuntimeException throwIfNotFound() {
				return null;
			}
		});
		if (a == null) {
			return false;
		}
		return true;
	}
	
	/**
	 * Searches email for a given message using wild card and normalized-spacing
	 * 
	 */
	public boolean verifyEmailNormalizeWithWildCard(String email, final String subject, final String body, boolean delete) {
		logToFile(subject + "\n body!!!\n" + body+ "\n...:");
		Msg a = searchEmail(email, new MsgTester() {

			@Override
			public boolean matchMessage(Msg m) {
				String msgBody = normalizeString(m.getBody());
				System.out.println("[[[[Inbox Content below]]]]:");
				System.out.println(msgBody);
				if (m.getSubject().trim().equals(subject.trim())) {
					if (wildCardMatchWithoutAsterisk(msgBody, body)) {
						if (delete) {
							m.delete();
						}
						System.out.println("[[[[Matched Success!]]]]:");
						System.out.println(msgBody);
						return true;
					} else {

					}
				}
				return false;
			}

			@Override
			public RuntimeException throwIfNotFound() {
				return null;
			}
		});
		if (a == null) {
			return false;
		}
		return true;
	}
	
	public boolean verifyEmailNormalizeWithWildCard(String email, final String subject, final String body) {
		return verifyEmailNormalizeWithWildCard(email, subject, body, true);
	}
	
	public boolean verifyEmailNormalizeWithWildCardWithoutDelete(String email, final String subject, final String body) {
		return verifyEmailNormalizeWithWildCard(email, subject, body, false);
	}

	/**
	 * Compares two strings with one being composed of wildcards. The string
	 * "(:wildcard:)" represents the wildcard.
	 * 
	 * @param text
	 * @param pattern
	 * @return
	 */
	public boolean wildCardMatchWithoutAsterisk(String text, String pattern) {
		if (text.length() < 1 && pattern.length() < 1) {
			return true;
		}
		if (text.length() < 1 || pattern.length() < 1) {
			return false;
		}
		String[] cards = pattern.split("\\(:wildcard:\\)");

		// Iterate over the cards.
		for (String card : cards) {
			int idx = text.indexOf(card);

			// Card not detected in the text.
			if (idx == -1) {
				logToFile("\ncard:\n" + card + "\n//\ntext:\n" + text + "//\nDiff: " + StringUtils.difference(pattern, text));
				Page.printEmailFormattedMessage("\n**********************This wildcard divided part is NOT the substring "
						+ "of what we've got in email inbox**********************\n\nWildcard divided part is as below: \n\n"
						+ card + "\n\n**********************What we've got in inbox is as below: **********************\n\n" + text);
				return false;
			}

			// Move ahead, towards the right of the text.
			text = text.substring(idx + card.length());
		}

		return true;
	}

	public File logFile = new File(Project.downloads("email.txt"));
	
	protected void logToFile(String string) {
		try {
			FileUtils.write(logFile, string + "\n", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
