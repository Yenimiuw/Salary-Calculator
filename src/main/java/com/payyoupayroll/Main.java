package com.payyoupayroll;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;

public class Main extends JFrame {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPane = new JPanel(new BorderLayout(10, 10));
	private DefaultTableModel tableModel;
	private JTable table;
	private String loggedInUser;

	private static final String SYSTEM_NAME = "PayYouPayroll";

	public static final Color THEME_PRIMARY = new Color(0, 56, 168);     
	public static final Color THEME_SECONDARY = new Color(242, 195, 0);   
	public static final Color THEME_DANGER = new Color(206, 17, 38);     
	public static final Color THEME_BG = new Color (245, 245, 245);       
	public static final Color THEME_TEXT = new Color(15, 23, 42);        

	public static final Font THEME_FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 12); 
	public static final Font THEME_FONT_BOLD = new Font("Segoe UI", Font.BOLD, 12);
	public static final Font THEME_FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);

	public static void applyGlobalTheme() {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		UIManager.put("Panel.background", THEME_BG);
		UIManager.put("OptionPane.background", THEME_BG);
		UIManager.put("OptionPane.messageFont", THEME_FONT_REGULAR);
		UIManager.put("OptionPane.messageForeground", THEME_TEXT);

		UIManager.put("Button.background", Color.WHITE);
		UIManager.put("Button.foreground", THEME_TEXT); 
		UIManager.put("Button.font", THEME_FONT_BOLD);
		UIManager.put("Button.focus", false);
		UIManager.put("Button.border", BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true), 
				BorderFactory.createEmptyBorder(6, 14, 6, 14) 
				));
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				applyGlobalTheme();
				try {
					DatabaseIntegration.initializeDatabase();
				} catch (SQLException e) {
					Exceptions.handleDatabaseErrorWithMessage(null, "Database connection failed", e);
					return;
				}

				Main frame = new Main();
				frame.setVisible(true);
			}
		});
	}

	public Main() {
		setTitle(SYSTEM_NAME + " - Payroll Management System");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1100, 620);
		setContentPane(contentPane);
		showWelcomeScreen();

		java.net.URL iconURL = getClass().getClassLoader().getResource("icon.png");
		if (iconURL != null) {
			this.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage(iconURL));
		}
	}

	private void showWelcomeScreen() {
		contentPane.removeAll();
		contentPane.setLayout(new GridBagLayout());
		contentPane.setBackground(new Color(241, 245, 249)); 

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1.0;

		JPanel leftHeroPanel = new JPanel();
		leftHeroPanel.setLayout(new BoxLayout(leftHeroPanel, BoxLayout.Y_AXIS));
		leftHeroPanel.setBackground(new Color(47, 82, 136)); 
		leftHeroPanel.setBorder(BorderFactory.createEmptyBorder(60, 40, 40, 40));

		JLabel brandLabel = new JLabel(SYSTEM_NAME);
		brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 42)); 
		brandLabel.setForeground(new Color(255, 241, 146));
		brandLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel brandSubtitle = new JLabel("Secure Payroll Operations");
		brandSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		brandSubtitle.setForeground(new Color(203, 213, 225)); 
		brandSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

		leftHeroPanel.add(brandLabel);
		leftHeroPanel.add(Box.createVerticalStrut(8));
		leftHeroPanel.add(brandSubtitle);

		gbc.gridx = 0;
		gbc.weightx = 0.38; 
		contentPane.add(leftHeroPanel, gbc);

		JPanel rightContainer = new JPanel(new GridBagLayout());
		rightContainer.setOpaque(false); 

		JPanel centralCard = new JPanel();
		centralCard.setLayout(new BoxLayout(centralCard, BoxLayout.Y_AXIS));
		centralCard.setBackground(Color.WHITE);
		centralCard.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
				BorderFactory.createEmptyBorder(45, 50, 45, 50)
				));

		JLabel welcomeLabel1 = new JLabel("Employee Payroll");
		welcomeLabel1.setFont(new Font("Segoe UI", Font.BOLD, 24));
		welcomeLabel1.setForeground(THEME_TEXT);
		welcomeLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel welcomeLabel2 = new JLabel("Management Desk");
		welcomeLabel2.setFont(new Font("Segoe UI", Font.BOLD, 24));
		welcomeLabel2.setForeground(THEME_TEXT);
		welcomeLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);

		centralCard.add(welcomeLabel1);
		centralCard.add(welcomeLabel2);
		centralCard.add(Box.createVerticalStrut(20));

		JPanel lineSeparator = new JPanel();
		lineSeparator.setMaximumSize(new Dimension(80, 3));
		lineSeparator.setPreferredSize(new Dimension(80, 3));
		lineSeparator.setBackground(new Color(47, 82, 136)); 
		lineSeparator.setAlignmentX(Component.CENTER_ALIGNMENT);
		centralCard.add(lineSeparator);
		centralCard.add(Box.createVerticalStrut(20));

		JLabel promptLabel = new JLabel("Select an option below to authenticate and manage records");
		promptLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		promptLabel.setForeground(new Color(100, 116, 139));
		promptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		centralCard.add(promptLabel);
		centralCard.add(Box.createVerticalStrut(30));

		JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
		buttonRow.setOpaque(false);

		JButton signInButton = new JButton("Sign In");
		signInButton.setFocusPainted(false);
		signInButton.setFont(THEME_FONT_BOLD);
		signInButton.setPreferredSize(new Dimension(140, 42));
		signInButton.setBackground(new Color(255, 255, 183)); 
		signInButton.setForeground(THEME_TEXT);
		signInButton.setBorder(BorderFactory.createLineBorder(new Color(234, 179, 8), 1)); 

		JButton signUpButton = new JButton("Sign Up");
		signUpButton.setFocusPainted(false);
		signUpButton.setFont(THEME_FONT_BOLD);
		signUpButton.setPreferredSize(new Dimension(140, 42));
		signUpButton.setBackground(new Color(255, 241, 146)); 
		signUpButton.setForeground(THEME_TEXT);
		signUpButton.setBorder(BorderFactory.createLineBorder(new Color(234, 179, 8), 1)); 

		signInButton.addActionListener(e -> showAuthPanel("signin"));
		signUpButton.addActionListener(e -> showAuthPanel("signup"));

		buttonRow.add(signInButton);
		buttonRow.add(signUpButton);
		centralCard.add(buttonRow);

		rightContainer.add(centralCard);

		gbc.gridx = 1;
		gbc.weightx = 0.62; 
		contentPane.add(rightContainer, gbc);

		contentPane.revalidate();
		contentPane.repaint();
	}

	private void showAuthPanel(String mode) {
		contentPane.removeAll();
		contentPane.setLayout(new GridBagLayout());
		contentPane.setBackground(new Color(241, 245, 249));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1.0;

		JPanel leftHeroPanel = new JPanel();
		leftHeroPanel.setLayout(new BoxLayout(leftHeroPanel, BoxLayout.Y_AXIS));
		leftHeroPanel.setBackground(new Color(47, 82, 136));
		leftHeroPanel.setBorder(BorderFactory.createEmptyBorder(60, 40, 40, 40));

		JLabel brandLabel = new JLabel(SYSTEM_NAME);
		brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
		brandLabel.setForeground(new Color(255, 241, 146));
		brandLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel brandSubtitle = new JLabel(mode.equals("signin") ? "Portal Authentication" : "Account Provisioning");
		brandSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		brandSubtitle.setForeground(new Color(203, 213, 225));
		brandSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

		leftHeroPanel.add(brandLabel);
		leftHeroPanel.add(Box.createVerticalStrut(8));
		leftHeroPanel.add(brandSubtitle);

		JTextField userField = new JTextField();
		userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
		userField.setPreferredSize(new Dimension(220, 35));

		JPasswordField passField = new JPasswordField();
		passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
		passField.setPreferredSize(new Dimension(220, 35));

		if (mode.equals("signup")) {
			leftHeroPanel.add(Box.createVerticalGlue());

			JPanel liveChecklistPanel = new JPanel();
			liveChecklistPanel.setLayout(new BoxLayout(liveChecklistPanel, BoxLayout.Y_AXIS));
			liveChecklistPanel.setOpaque(false);
			liveChecklistPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

			JLabel checklistTitle = new JLabel("LIVE SECURITY CHECKLIST");
			checklistTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
			checklistTitle.setForeground(new Color(255, 241, 146));
			liveChecklistPanel.add(checklistTitle);
			liveChecklistPanel.add(Box.createVerticalStrut(15));

			JLabel reqLength = new JLabel("○ Minimum 8 characters");
			JLabel reqNumber = new JLabel("○ Contains at least 1 number");
			JLabel reqUpper  = new JLabel("○ Contains 1 uppercase letter");

			JLabel[] requirements = {reqLength, reqNumber, reqUpper};
			for (JLabel req : requirements) {
				req.setFont(new Font("Segoe UI", Font.PLAIN, 13));
				req.setForeground(new Color(241, 245, 249));
				req.setAlignmentX(Component.LEFT_ALIGNMENT);
				liveChecklistPanel.add(req);
				liveChecklistPanel.add(Box.createVerticalStrut(8));
			}

			leftHeroPanel.add(liveChecklistPanel);
			leftHeroPanel.add(Box.createVerticalGlue());

			passField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
				private void checkPassword() {
					String password = new String(passField.getPassword());

					if (password.length() >= 8) {
						reqLength.setText("✓ Minimum 8 characters");
						reqLength.setForeground(new Color(187, 247, 208));
					} else {
						reqLength.setText("○ Minimum 8 characters");
						reqLength.setForeground(new Color(241, 245, 249));
					}

					if (password.matches(".*\\d.*")) {
						reqNumber.setText("✓ Contains at least 1 number");
						reqNumber.setForeground(new Color(187, 247, 208));
					} else {
						reqNumber.setText("○ Contains at least 1 number");
						reqNumber.setForeground(new Color(241, 245, 249));
					}

					if (password.matches(".*[A-Z].*")) {
						reqUpper.setText("✓ Contains 1 uppercase letter");
						reqUpper.setForeground(new Color(187, 247, 208));
					} else {
						reqUpper.setText("○ Contains 1 uppercase letter");
						reqUpper.setForeground(new Color(241, 245, 249));
					}
				}
				public void insertUpdate(javax.swing.event.DocumentEvent e) { checkPassword(); }
				public void removeUpdate(javax.swing.event.DocumentEvent e) { checkPassword(); }
				public void changedUpdate(javax.swing.event.DocumentEvent e) { checkPassword(); }
			});
		}

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.4;
		contentPane.add(leftHeroPanel, gbc);

		JPanel rightFormPanel = new JPanel(new GridBagLayout());
		rightFormPanel.setOpaque(false);

		GridBagConstraints formGbc = new GridBagConstraints();
		formGbc.gridx = 0;
		formGbc.gridy = 0;
		formGbc.insets = new Insets(10, 10, 10, 10);
		formGbc.anchor = GridBagConstraints.WEST;

		JLabel errorLabel = new JLabel(" ");
		errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
		errorLabel.setForeground(new Color(188, 22, 44));
		rightFormPanel.add(errorLabel, formGbc);
		formGbc.gridy++;

		rightFormPanel.add(new JLabel("Username:"), formGbc);
		formGbc.gridy++;
		rightFormPanel.add(userField, formGbc);
		formGbc.gridy++;
		rightFormPanel.add(new JLabel("Password:"), formGbc);
		formGbc.gridy++;
		rightFormPanel.add(passField, formGbc);
		formGbc.gridy++;

		JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		buttonRow.setOpaque(false);

		JButton actionButton = new JButton(mode.equals("signin") ? "Sign In" : "Create Account");
		buttonRow.add(actionButton);

		JButton cancelButton = new JButton("Back to Welcome");
		cancelButton.setBackground(new Color(148, 163, 184));
		cancelButton.setForeground(Color.WHITE);
		cancelButton.addActionListener(e -> showWelcomeScreen());
		buttonRow.add(cancelButton);

		rightFormPanel.add(buttonRow, formGbc);

		actionButton.addActionListener(e -> {
			String username = userField.getText().trim();
			String password = new String(passField.getPassword());

			if (username.isEmpty() || password.isEmpty()) {
				errorLabel.setText("⚠️ Submission blocked: Fields cannot be blank.");
				return;
			}

			if (mode.equals("signin")) {
				loggedInUser = username;
				showMainMenu();
			} else if (mode.equals("signup")) {
				rightFormPanel.removeAll();

				GridBagConstraints successGbc = new GridBagConstraints();
				successGbc.gridx = 0;
				successGbc.gridy = 0;
				successGbc.insets = new Insets(12, 10, 12, 10);
				successGbc.anchor = GridBagConstraints.CENTER;

				JLabel successTitle = new JLabel("Account Created.");
				successTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
				successTitle.setForeground(new Color(34, 197, 94));
				rightFormPanel.add(successTitle, successGbc);

				successGbc.gridy++;
				JLabel successSub = new JLabel("Your security credentials are fully provisioned.");
				successSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
				rightFormPanel.add(successSub, successGbc);

				successGbc.gridy++;
				JButton loginRedirectButton = new JButton("Go to Sign In");
				loginRedirectButton.setPreferredSize(new Dimension(200, 35));
				loginRedirectButton.setBackground(new Color(47, 82, 136));
				loginRedirectButton.setForeground(Color.WHITE);
				rightFormPanel.add(loginRedirectButton, successGbc);

				successGbc.gridy++;
				JButton backToWelcomeButton = new JButton("Back to Welcome");
				backToWelcomeButton.setPreferredSize(new Dimension(200, 35));
				backToWelcomeButton.setBackground(new Color(188, 22, 44));
				backToWelcomeButton.setForeground(Color.WHITE);
				rightFormPanel.add(backToWelcomeButton, successGbc);

				loginRedirectButton.addActionListener(ev -> showAuthPanel("signin"));
				backToWelcomeButton.addActionListener(ev -> showWelcomeScreen());

				rightFormPanel.revalidate();
				rightFormPanel.repaint();
			}
		});

		gbc.gridx = 1;
		gbc.weightx = 0.6;
		contentPane.add(rightFormPanel, gbc);

		revalidate();
		repaint();
	}

	private void showMainMenu() {
		contentPane.removeAll();
		contentPane.setLayout(new BorderLayout());
		contentPane.setBackground(THEME_BG); 

		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(new Color(47, 82, 136));
		headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

		JLabel systemLabel = new JLabel(SYSTEM_NAME);
		systemLabel.setFont(new Font("Arial", Font.BOLD, 24));
		systemLabel.setForeground(new Color(255, 241, 146));

		JLabel welcomeLabel = new JLabel("Main Menu");
		welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
		welcomeLabel.setForeground(Color.WHITE);

		JPanel leftHeader = new JPanel();
		leftHeader.setLayout(new BoxLayout(leftHeader, BoxLayout.Y_AXIS));
		leftHeader.setOpaque(false);
		leftHeader.add(systemLabel);
		leftHeader.add(Box.createVerticalStrut(4));
		leftHeader.add(welcomeLabel);

		JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		rightHeader.setOpaque(false);
		JLabel userLabel = new JLabel("Signed in as: " + loggedInUser);
		userLabel.setFont(THEME_FONT_BOLD);
		userLabel.setForeground(Color.WHITE);

		JButton logoutButton = new JButton("Logout");
		logoutButton.setFocusPainted(false);
		logoutButton.setFont(THEME_FONT_BOLD);
		logoutButton.setBackground(new Color(188, 22, 44));
		logoutButton.setForeground(Color.WHITE);
		logoutButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				loggedInUser = null;
				showWelcomeScreen();
			}
		});

		rightHeader.add(userLabel);
		rightHeader.add(logoutButton);

		headerPanel.add(leftHeader, BorderLayout.WEST);
		headerPanel.add(rightHeader, BorderLayout.EAST);
		contentPane.add(headerPanel, BorderLayout.NORTH);

		JPanel splitBodyPanel = new JPanel(new GridBagLayout());
		splitBodyPanel.setOpaque(false);
		contentPane.add(splitBodyPanel, BorderLayout.CENTER);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1.0;

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setBackground(Color.WHITE);
		leftPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(226, 232, 240)),
				BorderFactory.createEmptyBorder(20, 20, 20, 20)
				));

		JLabel menuTitle = new JLabel("WORKSPACE NAVIGATION");
		menuTitle.setFont(new Font("Arial", Font.BOLD, 12));
		menuTitle.setForeground(new Color(100, 116, 139));
		menuTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		leftPanel.add(menuTitle);
		leftPanel.add(Box.createVerticalStrut(15));

		JButton addButton = new JButton("➕  Add Employee");
		JButton viewButton = new JButton("👤  View Employee");
		JButton searchButton = new JButton("🔍  Search Employee");
		JButton updateButton = new JButton("📝  Update Employee");
		JButton deleteButton = new JButton("❌  Delete Employee");
		JButton recordsButton = new JButton("📊  View Records");

		JButton[] navButtons = {addButton, viewButton, searchButton, updateButton, deleteButton, recordsButton};

		for (JButton btn : navButtons) {
			btn.setFocusPainted(false);
			btn.setFont(THEME_FONT_BOLD);
			btn.setBackground(new Color(47, 82, 136)); 
			btn.setForeground(Color.WHITE);
			btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
			btn.setPreferredSize(new Dimension(200, 40));
			btn.setAlignmentX(Component.LEFT_ALIGNMENT);
			leftPanel.add(btn);
			leftPanel.add(Box.createVerticalStrut(10));
		}

		addButton.addActionListener(e -> showAddEmployeeScreen());
		viewButton.addActionListener(e -> viewEmployeeFlow());
		searchButton.addActionListener(e -> searchEmployeeFlow());
		updateButton.addActionListener(e -> updateEmployeeFlow());
		deleteButton.addActionListener(e -> deleteEmployeeFlow());
		recordsButton.addActionListener(e -> showViewRecordsScreen());

		leftPanel.add(Box.createVerticalGlue());
		JSeparator sep = new JSeparator();
		sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
		leftPanel.add(sep);
		leftPanel.add(Box.createVerticalStrut(15));

		JLabel statsTitle = new JLabel("SYSTEM METRICS");
		statsTitle.setFont(new Font("Arial", Font.BOLD, 11));
		statsTitle.setForeground(new Color(148, 163, 184));
		leftPanel.add(statsTitle);
		leftPanel.add(Box.createVerticalStrut(6));

		JLabel activeEmpLbl = new JLabel("• Active Employees Tracked: Connected");
		activeEmpLbl.setFont(THEME_FONT_REGULAR);
		activeEmpLbl.setForeground(THEME_TEXT);
		leftPanel.add(activeEmpLbl);

		gbc.gridx = 0;
		gbc.weightx = 0.35; 
		splitBodyPanel.add(leftPanel, gbc);

		JPanel rightCanvas = new JPanel(new GridLayout(1, 2, 20, 0));
		rightCanvas.setBackground(THEME_BG);
		rightCanvas.setBorder(new EmptyBorder(25, 25, 25, 25));

		JPanel schedulePanel = new JPanel();
		schedulePanel.setLayout(new BoxLayout(schedulePanel, BoxLayout.Y_AXIS));
		schedulePanel.setBackground(Color.WHITE);
		schedulePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
				BorderFactory.createEmptyBorder(20, 20, 20, 20)
				));

		JLabel schedTitle = new JLabel("📅  PAYROLL CYCLE POLICIES");
		schedTitle.setFont(new Font("Arial", Font.BOLD, 14));
		schedTitle.setForeground(new Color(47, 82, 136)); 
		schedTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		schedulePanel.add(schedTitle);
		schedulePanel.add(Box.createVerticalStrut(15));

		String[] policies = {
				"Standard Semi-Monthly Distribution",
				" First Period: 1st to 15th of the Month",
				" Second Period: 16th to End of the Month",
				"",
				"Standard Work Parameters",
				" Core Office Hours: 8.00 Hours / Day",
				" Regular Workdays: Monday to Friday",
				" Overtime Threshold: > 8.00 Hours / Day",
				"",
				"Holiday Payment Multipliers",
				" Regular Holidays: 200% Base Rate",
				" Special Non-Working Days: 130% Base Rate"
		};

		for (String line : policies) {
			JLabel lbl = new JLabel(line);
			lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
			if (line.endsWith("Distribution") || line.endsWith("Parameters") || line.endsWith("Multipliers")) {
				lbl.setFont(new Font("Arial", Font.BOLD, 12));
				lbl.setForeground(THEME_TEXT);
			} else {
				lbl.setFont(new Font("Arial", Font.PLAIN, 12));
				lbl.setForeground(new Color(100, 116, 139));
			}
			schedulePanel.add(lbl);
			schedulePanel.add(Box.createVerticalStrut(5));
		}

		JPanel compliancePanel = new JPanel();
		compliancePanel.setLayout(new BoxLayout(compliancePanel, BoxLayout.Y_AXIS));
		compliancePanel.setBackground(Color.WHITE);
		compliancePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
				BorderFactory.createEmptyBorder(20, 20, 20, 20)
				));

		JLabel compTitle = new JLabel("🏛️  STATUTORY DEDUCTIONS GUIDE");
		compTitle.setFont(new Font("Arial", Font.BOLD, 14));
		compTitle.setForeground(new Color(47, 82, 136)); 
		compTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		compliancePanel.add(compTitle);
		compliancePanel.add(Box.createVerticalStrut(15));

		String[] statutoryInfo = {
				"Mandatory Agency Contributions",
				" Social Security System Contribution (SSS)",
				" Philippine Health Insurance Corp. (PHILHEALTH)",
				" Home Development Mutual Fund (Pag-IBIG)",
				"",
				"Withholding Tax Schedule",
				" Bureau of Internal Revenue (BIR) Table",
				" Derived dynamically using annualized basic pay",
				"",
				"System Integrity Notice",
				" Calculations sync directly to structural database.",
				" Deductions process automatically upon calculation."
		};

		for (String line : statutoryInfo) {
			JLabel lbl = new JLabel(line);
			lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
			if (line.endsWith("Contributions") || line.endsWith("Schedule") || line.endsWith("Notice")) {
				lbl.setFont(new Font("Arial", Font.BOLD, 12));
				lbl.setForeground(THEME_TEXT);
			} else {
				lbl.setFont(new Font("Arial", Font.PLAIN, 12));
				lbl.setForeground(new Color(100, 116, 139));
			}
			compliancePanel.add(lbl);
			compliancePanel.add(Box.createVerticalStrut(5));
		}

		rightCanvas.add(schedulePanel);
		rightCanvas.add(compliancePanel);

		gbc.gridx = 1;
		gbc.weightx = 0.65; 
		splitBodyPanel.add(rightCanvas, gbc);

		contentPane.revalidate();
		contentPane.repaint();
	}


	private void showAddEmployeeScreen() {
		contentPane.removeAll();
		contentPane.setLayout(new BorderLayout(12, 12));
		contentPane.setBorder(new EmptyBorder(12, 12, 12, 12));
		contentPane.setBackground(THEME_BG);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerLocation(390);
		splitPane.setDividerSize(6);
		splitPane.setBorder(BorderFactory.createEmptyBorder());
		splitPane.setBackground(THEME_BG);

		JPanel leftPanel = new JPanel();
		leftPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
				new EmptyBorder(20, 20, 20, 20)
				));
		leftPanel.setBackground(Color.WHITE);
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

		JLabel title = new JLabel("Add New Employee");
		title.setFont(THEME_FONT_TITLE);
		title.setForeground(THEME_PRIMARY); 
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		title.setBorder(new EmptyBorder(0, 0, 15, 0));

		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBackground(Color.WHITE);
		formPanel.setMaximumSize(new Dimension(380, 280));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(6, 6, 6, 6);

		JTextField nameField = new JTextField();
		JTextField numberField = new JTextField();
		JTextField deptField = new JTextField();
		JTextField salaryField = new JTextField();

		nameField.setFont(THEME_FONT_REGULAR);
		numberField.setFont(THEME_FONT_REGULAR);
		deptField.setFont(THEME_FONT_REGULAR);
		salaryField.setFont(THEME_FONT_REGULAR);

		gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; gbc.anchor = GridBagConstraints.WEST;
		JLabel nameLabel = new JLabel("Employee Name:");
		nameLabel.setFont(THEME_FONT_BOLD);
		nameLabel.setForeground(THEME_TEXT);
		formPanel.add(nameLabel, gbc);
		gbc.gridx = 1; gbc.weightx = 1.0;
		formPanel.add(nameField, gbc);

		gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
		JLabel numLabel = new JLabel("Employee ID Number:");
		numLabel.setFont(THEME_FONT_BOLD);
		numLabel.setForeground(THEME_TEXT);
		formPanel.add(numLabel, gbc);
		gbc.gridx = 1; gbc.weightx = 1.0;
		formPanel.add(numberField, gbc);

		gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
		JLabel deptLabel = new JLabel("Department:");
		deptLabel.setFont(THEME_FONT_BOLD);
		deptLabel.setForeground(THEME_TEXT);
		formPanel.add(deptLabel, gbc);
		gbc.gridx = 1; gbc.weightx = 1.0;
		formPanel.add(deptField, gbc);

		gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
		JLabel salaryLabel = new JLabel("Monthly Base Pay:");
		salaryLabel.setFont(THEME_FONT_BOLD);
		salaryLabel.setForeground(THEME_TEXT);
		formPanel.add(salaryLabel, gbc);
		gbc.gridx = 1; gbc.weightx = 1.0;
		formPanel.add(salaryField, gbc);

		gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 1.0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(12, 6, 4, 6);
		JLabel lblWorkingDays = new JLabel("Annual Schedule (Working Days):");
		lblWorkingDays.setFont(THEME_FONT_BOLD);
		lblWorkingDays.setForeground(THEME_TEXT);
		formPanel.add(lblWorkingDays, gbc);

		JRadioButton r261 = new JRadioButton("261 Days (Mon - Fri)");
		r261.setFont(THEME_FONT_REGULAR);
		r261.setBackground(Color.WHITE);
		r261.setFocusPainted(false);

		JRadioButton r312 = new JRadioButton("312 Days (Mon - Sat)");
		r312.setFont(THEME_FONT_REGULAR);
		r312.setBackground(Color.WHITE);
		r312.setFocusPainted(false);

		JRadioButton rOther = new JRadioButton("Custom Factor");
		rOther.setFont(THEME_FONT_REGULAR);
		rOther.setBackground(Color.WHITE);
		rOther.setFocusPainted(false);

		ButtonGroup wdGroup = new ButtonGroup();
		wdGroup.add(r261);
		wdGroup.add(r312);
		wdGroup.add(rOther);

		JTextField customWdField = new JTextField();
		customWdField.setFont(THEME_FONT_REGULAR);
		customWdField.setEnabled(false);
		customWdField.setPreferredSize(new Dimension(75, 24));
		customWdField.setMaximumSize(new Dimension(75, 24));

		rOther.addActionListener(e -> customWdField.setEnabled(rOther.isSelected()));
		r261.addActionListener(e -> { customWdField.setEnabled(false); customWdField.setText(""); });
		r312.addActionListener(e -> { customWdField.setEnabled(false); customWdField.setText(""); });

		JPanel radioPanel = new JPanel();
		radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
		radioPanel.setBackground(Color.WHITE);
		
		r261.setAlignmentX(Component.LEFT_ALIGNMENT);
		r312.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		radioPanel.add(r261);
		radioPanel.add(r312);

		JPanel otherRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		otherRow.setBackground(Color.WHITE);
		otherRow.setAlignmentX(Component.LEFT_ALIGNMENT);
		otherRow.add(rOther);
		otherRow.add(Box.createHorizontalStrut(8));
		otherRow.add(customWdField);
		radioPanel.add(otherRow);

		gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 1.0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(2, 16, 6, 6);
		formPanel.add(radioPanel, gbc);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
		buttonPanel.setBackground(Color.WHITE);

		JButton btnAdd = new JButton("Next: Calculate Pay");
		btnAdd.setFocusPainted(false);
		btnAdd.setFont(THEME_FONT_BOLD);
		btnAdd.setBackground(THEME_SECONDARY);
		btnAdd.setForeground(THEME_TEXT);
		btnAdd.setPreferredSize(new Dimension(160, 36));

		JButton btnBack = new JButton("Main Menu");
		btnBack.setFocusPainted(false);
		btnBack.setFont(THEME_FONT_BOLD);
		btnBack.setBackground(Color.WHITE);
		btnBack.setForeground(THEME_TEXT);
		btnBack.setPreferredSize(new Dimension(130, 36));

		btnAdd.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				try {
					String name = nameField.getText().trim();
					String number = numberField.getText().trim();
					String department = deptField.getText().trim();
					if (name.isEmpty() || number.isEmpty() || department.isEmpty()) {
						JOptionPane.showMessageDialog(Main.this, "Please fill in the employee name, number, and department.", "Missing Data", JOptionPane.WARNING_MESSAGE);
						return;
					}

					if (DatabaseIntegration.employeeExists(number)) {
						int choice = JOptionPane.showConfirmDialog(Main.this,
								"Employee ID Number already exists. Do you want to overwrite this existing entry?",
								"Duplicate Record Detected",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.WARNING_MESSAGE);

						if (choice == JOptionPane.YES_OPTION) {
							DatabaseIntegration.deleteEmployee(number);
							JOptionPane.showMessageDialog(Main.this, "Existing record cleared. Click 'Next' again to confirm creation.");
						}
						return;
					}

					double monthlySalary = parseDouble(salaryField.getText(), "Monthly Salary");
					double workingDaysPerYear;
					if (r261.isSelected()) {
						workingDaysPerYear = 261;
					} else if (r312.isSelected()) {
						workingDaysPerYear = 312;
					} else if (rOther.isSelected()) {
						String cv = customWdField.getText().trim();
						if (cv.isEmpty()) {
							JOptionPane.showMessageDialog(Main.this, "Please enter a custom working days value.", "Validation Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
						workingDaysPerYear = parseDouble(cv, "Working Days");
					} else {
						JOptionPane.showMessageDialog(Main.this, "Please choose a working days option.", "Validation Error", JOptionPane.ERROR_MESSAGE);
						return;
					}

					showPayrollDialog(name, number, department, monthlySalary, workingDaysPerYear);
					loadRecords();

					nameField.setText("");
					numberField.setText("");
					deptField.setText("");
					salaryField.setText("");
					wdGroup.clearSelection();
					customWdField.setText("");
					customWdField.setEnabled(false);

				} catch (Exception ex) {
					Exceptions.handleInputError(Main.this, ex);
				}
			}
		});

		btnBack.addActionListener(e -> showMainMenu());
		buttonPanel.add(btnAdd);
		buttonPanel.add(btnBack);

		leftPanel.add(title);
		leftPanel.add(Box.createVerticalStrut(8));
		leftPanel.add(formPanel);
		leftPanel.add(Box.createVerticalStrut(15));
		leftPanel.add(buttonPanel);
		leftPanel.add(Box.createVerticalGlue());

		JPanel rightPanel = new JPanel(new BorderLayout(6, 6));
		rightPanel.setBackground(THEME_BG);

		JLabel tableTitle = new JLabel("Live Database Registry");
		tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
		tableTitle.setForeground(THEME_TEXT);
		tableTitle.setBorder(new EmptyBorder(4, 6, 6, 4));
		rightPanel.add(tableTitle, BorderLayout.NORTH);

		setupTableStructure();
		JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
		rightPanel.add(scrollPane, BorderLayout.CENTER);

		splitPane.setLeftComponent(leftPanel);
		splitPane.setRightComponent(rightPanel);
		contentPane.add(splitPane, BorderLayout.CENTER);

		loadRecords();
		revalidate();
		repaint();
	}

	private void showViewRecordsScreen() {
		contentPane.removeAll();
		contentPane.setLayout(new BorderLayout(12, 12));
		contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
		contentPane.setBackground(THEME_BG);

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);

		JLabel lblRecords = new JLabel("Master Payroll Registry");
		lblRecords.setFont(THEME_FONT_TITLE);
		lblRecords.setForeground(THEME_PRIMARY); 
		lblRecords.setBorder(new EmptyBorder(0, 0, 8, 0));
		topPanel.add(lblRecords, BorderLayout.WEST);
		contentPane.add(topPanel, BorderLayout.NORTH);

		setupTableStructure();
		JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
		contentPane.add(scrollPane, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
		bottomPanel.setOpaque(false);
		bottomPanel.setBorder(new EmptyBorder(12, 0, 0, 0));

		JButton btnGenerateSummary = new JButton("Generate Summary Cards");
		btnGenerateSummary.setFocusPainted(false);
		btnGenerateSummary.setAlignmentX(CENTER_ALIGNMENT);
		btnGenerateSummary.setFont(THEME_FONT_BOLD);
		btnGenerateSummary.setBackground(THEME_PRIMARY); 
		btnGenerateSummary.setForeground(Color.WHITE);
		btnGenerateSummary.setPreferredSize(new Dimension(200, 38));
		btnGenerateSummary.addActionListener(e -> generateSummaryFlow());

		JButton btnBack = new JButton("Return to Dashboard");
		btnBack.setFocusPainted(false);
		btnBack.setAlignmentX(CENTER_ALIGNMENT);
		btnBack.setFont(THEME_FONT_BOLD);
		btnBack.setBackground(Color.WHITE);
		btnBack.setForeground(THEME_TEXT);
		btnBack.setPreferredSize(new Dimension(200, 34));
		btnBack.addActionListener(e -> showMainMenu());

		JPanel genPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		genPanel.setOpaque(false);
		genPanel.add(btnGenerateSummary);

		JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		backPanel.setOpaque(false);
		backPanel.add(btnBack);

		bottomPanel.add(genPanel);
		bottomPanel.add(Box.createVerticalStrut(4));
		bottomPanel.add(backPanel);

		contentPane.add(bottomPanel, BorderLayout.SOUTH);

		loadRecords();
		revalidate();
		repaint();
	}

	private void setupTableStructure() {
		String[] columns = {
				"Employee Name", "Employee ID", "Department",
				"Monthly Pay", "Daily Rate", "Hourly Rate", "Working Days/Year",
				"Regular Days", "Regular Hours", "Regular Day Pay",
				"Special Days", "Special Hours", "Special Day Pay",
				"Holiday Days", "Holiday Hours", "Holiday Day Pay",
				"Total Days", "Total Overtime", "Total Base Pay",
				"SSS", "PhilHealth", "Pag-IBIG", "Tax",
				"Total Deductions", "Net Take-Home Pay"
		};
		tableModel = new DefaultTableModel(columns, 0) {
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table = new JTable(tableModel);
		table.setRowHeight(26);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setFont(THEME_FONT_REGULAR);
		table.setBackground(Color.WHITE);
		table.setGridColor(new Color(241, 245, 249));

		table.getTableHeader().setFont(THEME_FONT_BOLD);
		table.getTableHeader().setBackground(THEME_PRIMARY);
		table.getTableHeader().setForeground(Color.WHITE);
		table.getTableHeader().setReorderingAllowed(false);

		int[] colWidths = {
				150, 110, 110, 110, 100, 100, 120, 100, 100, 110,
				100, 100, 110, 100, 100, 110, 100, 110, 110, 85,
				95, 85, 85, 120, 160
		};
		for (int i = 0; i < colWidths.length && i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(colWidths[i]);
		}
	}

	private void generateSummaryFlow() {
		try {
			List<DatabaseIntegration.EmployeeRecord> records = DatabaseIntegration.getAllEmployeeRecords();
			if (records.isEmpty()) {
				JOptionPane.showMessageDialog(this, "No employee records found.", "Generate Summary", JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			JPanel listPanel = new JPanel();
			listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
			listPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
			listPanel.setBackground(THEME_BG); 

			int count = 1;
			for (DatabaseIntegration.EmployeeRecord rec : records) {
				JPanel empPanel = new JPanel();
				empPanel.setLayout(new BoxLayout(empPanel, BoxLayout.Y_AXIS));
				empPanel.setBackground(Color.WHITE); 
				empPanel.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createLineBorder(new Color(226, 232, 240)), 
						"Employee " + count + " - " + rec.name, 
						javax.swing.border.TitledBorder.LEFT, 
						javax.swing.border.TitledBorder.TOP, 
						THEME_FONT_BOLD, 
						THEME_PRIMARY    
						));

				JPanel gridPanel = new JPanel(new GridLayout(0, 2, 10, 8));
				gridPanel.setOpaque(false); 
				gridPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

				gridPanel.add(createSummaryField("Monthly Rate:", String.format("%.2f", rec.monthlyPay)));
				gridPanel.add(createSummaryField("Daily Rate:", String.format("%.2f", rec.dailyRate)));
				gridPanel.add(createSummaryField("Hourly Rate:", String.format("%.2f", rec.hourlyRate)));
				gridPanel.add(new JLabel("")); 

				gridPanel.add(createSummaryField("Regular hours total:", String.format("%.2f", rec.regularHours)));
				gridPanel.add(createSummaryField("Regular day pay:", String.format("%.2f", rec.regularDayPay)));

				gridPanel.add(createSummaryField("Holiday hours total:", String.format("%.2f", rec.holidayHours)));
				gridPanel.add(createSummaryField("Holiday day pay:", String.format("%.2f", rec.holidayDayPay)));

				gridPanel.add(createSummaryField("Special hours total:", String.format("%.2f", rec.specialHours)));
				gridPanel.add(createSummaryField("Special day pay:", String.format("%.2f", rec.specialDayPay)));

				gridPanel.add(createSummaryField("Overtime hours in total:", String.format("%.2f", rec.totalOvertime)));
				gridPanel.add(createSummaryField("Total base pay:", String.format("%.2f", rec.totalBasePay)));

				gridPanel.add(createSummaryField("SSS Deduction:", String.format("%.2f", rec.sss)));
				gridPanel.add(createSummaryField("PhilHealth Deduction:", String.format("%.2f", rec.philhealth)));

				gridPanel.add(createSummaryField("Pag-Ibig Deduction:", String.format("%.2f", rec.pagibig)));
				gridPanel.add(createSummaryField("Total Deductions:", String.format("%.2f", rec.totalDeductions)));

				gridPanel.add(createSummaryField("Total Salary this month:", String.format("%.2f", rec.totalSalaryThisMonth)));

				empPanel.add(gridPanel);
				listPanel.add(empPanel);
				listPanel.add(Box.createVerticalStrut(15));
				count++;
			}

			JDialog summaryDialog = new JDialog(this, "Employee Summary Report", true);
			summaryDialog.setSize(650, 600);
			summaryDialog.setLocationRelativeTo(this);
			summaryDialog.setLayout(new BorderLayout(10, 10));

			JScrollPane scrollPane = new JScrollPane(listPanel);
			scrollPane.getVerticalScrollBar().setUnitIncrement(16);
			scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			JButton btnClose = new JButton("Close");
			btnClose.setFocusPainted(false);
			btnClose.setFont(THEME_FONT_BOLD);
			btnClose.setBackground(THEME_SECONDARY); 
			btnClose.setForeground(THEME_TEXT);      
			btnClose.setPreferredSize(new Dimension(100, 35));
			btnClose.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent ev) {
					summaryDialog.dispose();
				}
			});

			JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			btnPanel.setBackground(THEME_BG); 
			btnPanel.add(btnClose);

			summaryDialog.add(scrollPane, BorderLayout.CENTER);
			summaryDialog.add(btnPanel, BorderLayout.SOUTH);
			summaryDialog.setVisible(true);

		} catch (SQLException ex) {
			Exceptions.handleDatabaseError(this, ex);
		}
	}

	private void showPayrollDialog(String employeeName, String employeeNumber, String department, double monthlySalary, double workingDaysPerYear) {
		JDialog dialog = new JDialog(this, "Calculating Employee Pay this Month", true);
		dialog.setSize(520, 440); 
		dialog.setLocationRelativeTo(this);
		dialog.setLayout(new BorderLayout(10, 10));

		JPanel content = new JPanel();
		content.setBorder(new EmptyBorder(18, 18, 18, 18));
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.setBackground(THEME_BG); 

		JLabel title = new JLabel("Calculating Employee Pay this Month");
		title.setFont(THEME_FONT_TITLE);
		title.setForeground(THEME_PRIMARY); 
		title.setAlignmentX(CENTER_ALIGNMENT);
		title.setBorder(new EmptyBorder(0, 0, 8, 0));

		JLabel info = new JLabel("Enter the employee's work details for this month.");
		info.setFont(THEME_FONT_REGULAR);
		info.setForeground(THEME_TEXT);
		info.setAlignmentX(CENTER_ALIGNMENT);
		info.setBorder(new EmptyBorder(0, 0, 12, 0));

		JPanel formPanel = new JPanel(new GridLayout(6, 2, 8, 8)); 
		formPanel.setOpaque(false);
		formPanel.setBorder(new EmptyBorder(5, 5, 12, 5));

		JTextField regularDaysField = new JTextField();
		JTextField regularHoursField = new JTextField();
		JTextField specialDaysField = new JTextField();
		JTextField specialHoursField = new JTextField();
		JTextField holidayDaysField = new JTextField();
		JTextField holidayHoursField = new JTextField();

		regularDaysField.setFont(THEME_FONT_REGULAR);
		regularHoursField.setFont(THEME_FONT_REGULAR);
		specialDaysField.setFont(THEME_FONT_REGULAR);
		specialHoursField.setFont(THEME_FONT_REGULAR);
		holidayDaysField.setFont(THEME_FONT_REGULAR);
		holidayHoursField.setFont(THEME_FONT_REGULAR);

		JLabel lbl1 = new JLabel("Regular Days Worked:"); lbl1.setFont(THEME_FONT_BOLD); lbl1.setForeground(THEME_TEXT);
		JLabel lbl2 = new JLabel("Regular Hours Worked:"); lbl2.setFont(THEME_FONT_BOLD); lbl2.setForeground(THEME_TEXT);
		JLabel lbl3 = new JLabel("Special Days Worked:"); lbl3.setFont(THEME_FONT_BOLD); lbl3.setForeground(THEME_TEXT);
		JLabel lbl4 = new JLabel("Special Hours Worked:"); lbl4.setFont(THEME_FONT_BOLD); lbl4.setForeground(THEME_TEXT);
		JLabel lbl5 = new JLabel("Holiday Days Worked:"); lbl5.setFont(THEME_FONT_BOLD); lbl5.setForeground(THEME_TEXT);
		JLabel lbl6 = new JLabel("Holiday Hours Worked:"); lbl6.setFont(THEME_FONT_BOLD); lbl6.setForeground(THEME_TEXT);

		formPanel.add(lbl1); formPanel.add(regularDaysField);
		formPanel.add(lbl2); formPanel.add(regularHoursField);
		formPanel.add(lbl3); formPanel.add(specialDaysField);
		formPanel.add(lbl4); formPanel.add(specialHoursField);
		formPanel.add(lbl5); formPanel.add(holidayDaysField);
		formPanel.add(lbl6); formPanel.add(holidayHoursField);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
		buttonPanel.setOpaque(false);

		JLabel resultLabel = new JLabel(" ");
		resultLabel.setFont(THEME_FONT_REGULAR);
		resultLabel.setForeground(THEME_TEXT);
		resultLabel.setAlignmentX(CENTER_ALIGNMENT);
		resultLabel.setBorder(new EmptyBorder(10, 0, 4, 0));

		JLabel payLabel = new JLabel(" ");
		payLabel.setFont(THEME_FONT_BOLD);
		payLabel.setForeground(new Color(21, 128, 61)); 
		payLabel.setAlignmentX(CENTER_ALIGNMENT);

		JButton calculateButton = new JButton("Calculate");
		calculateButton.setFocusPainted(false);
		calculateButton.setFont(THEME_FONT_BOLD);
		calculateButton.setBackground(THEME_SECONDARY); 
		calculateButton.setForeground(THEME_TEXT);
		calculateButton.setPreferredSize(new Dimension(120, 35));

		JButton closeButton = new JButton("Close");
		closeButton.setFocusPainted(false);
		closeButton.setFont(THEME_FONT_BOLD);
		closeButton.setBackground(Color.WHITE);
		closeButton.setForeground(THEME_TEXT);
		closeButton.setPreferredSize(new Dimension(100, 35));

		calculateButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				try {
					double regularDays = parseDoubleOrZero(regularDaysField.getText(), "Regular Days");
					double regularHours = parseDoubleOrZero(regularHoursField.getText(), "Regular Hours");
					double specialDays = parseDoubleOrZero(specialDaysField.getText(), "Special Days");
					double specialHours = parseDoubleOrZero(specialHoursField.getText(), "Special Hours");
					double holidayDays = parseDoubleOrZero(holidayDaysField.getText(), "Holiday Days");
					double holidayHours = parseDoubleOrZero(holidayHoursField.getText(), "Holiday Hours");

					BasePayCalculator calculator = new BasePayCalculator(monthlySalary, workingDaysPerYear);

					double avgRegHoursPerDay = regularHours / Math.max(1.0, regularDays);
					double avgSpecHoursPerDay = specialHours / Math.max(1.0, specialDays);
					double avgHolHoursPerDay = holidayHours / Math.max(1.0, holidayDays);

					double regularDayPay = regularDays * calculator.computeDailyEarnings(1, avgRegHoursPerDay);
					double specialDayPay = specialDays * calculator.computeDailyEarnings(2, avgSpecHoursPerDay);
					double holidayDayPay = holidayDays * calculator.computeDailyEarnings(3, avgHolHoursPerDay);

					double regOtHours = regularDays * Math.max(0, avgRegHoursPerDay - BasePayCalculator.STANDARD_HOURS);
					double specOtHours = specialDays * Math.max(0, avgSpecHoursPerDay - BasePayCalculator.STANDARD_HOURS);
					double holOtHours = holidayDays * Math.max(0, avgHolHoursPerDay - BasePayCalculator.STANDARD_HOURS);

					double totalOvertime = regOtHours + specOtHours + holOtHours;
					double totalBasePay = regularDayPay + specialDayPay + holidayDayPay; 
					double[] deductions = Deductions.calculateAll(totalBasePay);

					boolean added = false;
					try {
						added = DatabaseIntegration.addEmployee(employeeName, employeeNumber, department,
								monthlySalary, calculator.getDailyRate(), calculator.getHourlyRate(),
								regularHours, specialHours, holidayHours,
								regularDayPay, specialDayPay, holidayDayPay,
								regularDays + specialDays + holidayDays, totalOvertime, totalBasePay,
								deductions[0], deductions[1], deductions[2], deductions[3], deductions[4], deductions[5],
								workingDaysPerYear, regularDays, specialDays, holidayDays);
					} catch (Exception dbEx) {
						boolean handled = Exceptions.handleDuplicateEmployee(dialog, dbEx, employeeNumber, new Exceptions.GeneralAction() {
							public void execute() throws Exception {
								DatabaseIntegration.EmployeeRecord record = new DatabaseIntegration.EmployeeRecord();
								record.name = employeeName;
								record.number = employeeNumber;
								record.department = department;
								record.monthlyPay = monthlySalary;
								record.dailyRate = calculator.getDailyRate();
								record.hourlyRate = calculator.getHourlyRate();
								record.regularHours = regularHours;
								record.specialHours = specialHours;
								record.holidayHours = holidayHours;
								record.regularDayPay = regularDayPay;
								record.specialDayPay = specialDayPay;
								record.holidayDayPay = holidayDayPay;
								record.totalHours = regularDays + specialDays + holidayDays;
								record.totalOvertime = totalOvertime;
								record.totalBasePay = totalBasePay;
								record.sss = deductions[0];
								record.philhealth = deductions[1];
								record.pagibig = deductions[2];
								record.tax = deductions[3];
								record.totalDeductions = deductions[4];
								record.totalSalaryThisMonth = deductions[5];
								record.workingDaysYear = workingDaysPerYear;
								record.regularDays = regularDays;
								record.specialDays = specialDays;
								record.holidayDays = holidayDays;

								DatabaseIntegration.saveEmployeeRecord(record, employeeNumber);

								resultLabel.setText(String.format("SSS: ₱%.2f | PhilHealth: ₱%.2f | Pag-IBIG: ₱%.2f | Tax: ₱%.2f | Total Deductions: ₱%.2f", deductions[0], deductions[1], deductions[2], deductions[3], deductions[4]));
								resultLabel.setForeground(THEME_TEXT);
								payLabel.setText(String.format("Total Salary this month: ₱%.2f | Total Overtime (Hours): %.2f", deductions[5], totalOvertime));
								JOptionPane.showMessageDialog(dialog, "Employee details updated successfully!");

								regularDaysField.setText("");
								specialDaysField.setText("");
								holidayDaysField.setText("");
								regularHoursField.setText("");
								specialHoursField.setText("");
								holidayHoursField.setText("");
							}
						});

						if (handled) {
							return;
						}
						throw dbEx;
					}

					if (!added) {
						throw new IllegalStateException("Employee could not be saved to the database.");
					}

					resultLabel.setText(String.format("SSS: ₱%.2f | PhilHealth: ₱%.2f | Pag-IBIG: ₱%.2f | Tax: ₱%.2f | Total Deductions: ₱%.2f", deductions[0], deductions[1], deductions[2], deductions[3], deductions[4]));
					resultLabel.setForeground(THEME_TEXT);
					payLabel.setText(String.format("Total Salary this month: ₱%.2f | Total Overtime (Hours): %.2f", deductions[5], totalOvertime));
				} catch (Exception ex) {
					resultLabel.setText(ex.getMessage());
					resultLabel.setForeground(THEME_DANGER); 
					payLabel.setText("");
				}
			}
		});

		closeButton.addActionListener(e -> dialog.dispose());

		buttonPanel.add(calculateButton);
		buttonPanel.add(closeButton);

		content.add(title);
		content.add(info);
		content.add(formPanel);
		content.add(buttonPanel);
		content.add(resultLabel);
		content.add(payLabel);

		dialog.add(content, BorderLayout.CENTER);
		dialog.setVisible(true);
	}

	private void showEmployeeDetailTableDialog(String employeeName, String[][] tableData) {
		JDialog detailDialog = new JDialog(this, "Employee Record: " + employeeName, true);
		detailDialog.setSize(500, 580);
		detailDialog.setLocationRelativeTo(this);
		detailDialog.setLayout(new BorderLayout(10, 10));

		String[] columnNames = {"Payroll Field", "Value"};
		DefaultTableModel model = new DefaultTableModel(tableData, columnNames) {
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		JTable detailTable = new JTable(model);
		detailTable.setRowHeight(24); 
		detailTable.setFont(THEME_FONT_REGULAR);
		detailTable.setBackground(Color.WHITE);
		detailTable.setGridColor(new Color(241, 245, 249));

		detailTable.getTableHeader().setFont(THEME_FONT_BOLD);
		detailTable.getTableHeader().setBackground(THEME_PRIMARY); 
		detailTable.getTableHeader().setForeground(Color.WHITE);
		detailTable.setFillsViewportHeight(true);

		JScrollPane scrollPane = new JScrollPane(detailTable);
		scrollPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(10, 15, 5, 15),
				BorderFactory.createLineBorder(new Color(226, 232, 240))
				));

		JButton btnClose = new JButton("Close");
		btnClose.setFocusPainted(false);
		btnClose.setFont(THEME_FONT_BOLD);
		btnClose.setBackground(THEME_SECONDARY); 
		btnClose.setForeground(THEME_TEXT);
		btnClose.setPreferredSize(new Dimension(100, 35));
		btnClose.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				detailDialog.dispose();
			}
		});
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		btnPanel.setBackground(THEME_BG);
		btnPanel.add(btnClose);

		detailDialog.add(scrollPane, BorderLayout.CENTER);
		detailDialog.add(btnPanel, BorderLayout.SOUTH);
		detailDialog.setVisible(true);
	}

	private void viewEmployeeFlow() {
		try {
			String identifier = JOptionPane.showInputDialog(this, "Enter Employee Name or Number to view:");
			if (identifier == null || identifier.trim().isEmpty()) {
				return;
			}
			String[][] tableData = DatabaseIntegration.getEmployeeDetailsAsTable(identifier.trim());
			if (tableData == null) {
				JOptionPane.showMessageDialog(this, "Employee not found.", "View Employee", JOptionPane.WARNING_MESSAGE);
			} else {
				String name = "";
				for (String[] row : tableData) {
					if (row[0].equals("Employee Name")) {
						name = row[1];
						break;
					}
				}
				showEmployeeDetailTableDialog(name, tableData);
			}
		} catch (SQLException ex) {
			Exceptions.handleDatabaseError(this, ex);
		}
	}

	private void searchEmployeeFlow() {
		try {
			String identifier = JOptionPane.showInputDialog(this, "Enter Employee Number or Name to search:");
			if (identifier == null || identifier.trim().isEmpty()) {
				return;
			}
			String[] info = DatabaseIntegration.findEmployeeNameAndNumber(identifier.trim());
			if (info == null) {
				JOptionPane.showMessageDialog(this, "Employee not found.", "Search Employee", JOptionPane.WARNING_MESSAGE);
				return;
			}

			String[] options = {"View", "Update", "Cancel"};
			int choice = JOptionPane.showOptionDialog(
					this,
					"Employee found: " + info[0] + " (" + info[1] + ")\nWhat would you like to do?",
					"Employee Found",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[0]
					);

			if (choice == 0) { 
				String[][] tableData = DatabaseIntegration.getEmployeeDetailsAsTable(info[1]);
				if (tableData != null) {
					showEmployeeDetailTableDialog(info[0], tableData);
				}
			} else if (choice == 1) { 
				performUpdateForEmployee(info[1]);
			}
		} catch (SQLException ex) {
			Exceptions.handleDatabaseError(this, ex);
		}
	}

	private void updateEmployeeFlow() {
		String identifier = JOptionPane.showInputDialog(this, "Enter Employee Number or Name to update:");
		if (identifier == null || identifier.trim().isEmpty()) {
			return;
		}
		try {
			String[] info = DatabaseIntegration.findEmployeeNameAndNumber(identifier.trim());
			if (info == null) {
				JOptionPane.showMessageDialog(this, "Employee not found.", "Update Employee", JOptionPane.WARNING_MESSAGE);
				return;
			}
			performUpdateForEmployee(info[1]);
		} catch (SQLException ex) {
			Exceptions.handleDatabaseError(this, ex);
		}
	}

	private void performUpdateForEmployee(String employeeNumber) {
		try {
			DatabaseIntegration.EmployeeRecord record = DatabaseIntegration.getEmployeeRecord(employeeNumber);
			if (record == null) {
				JOptionPane.showMessageDialog(this, "Employee record could not be loaded.");
				return;
			}

			JDialog checkboxDialog = new JDialog(this, "Select Fields to Update", true);
			checkboxDialog.setSize(400, 500);
			checkboxDialog.setLocationRelativeTo(this);
			checkboxDialog.setLayout(new BorderLayout(10, 10));

			JPanel listPanel = new JPanel();
			listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
			listPanel.setBackground(Color.WHITE);
			listPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

			JLabel instruction = new JLabel("Check the fields you want to update:");
			instruction.setFont(THEME_FONT_BOLD);
			instruction.setForeground(THEME_PRIMARY);
			instruction.setAlignmentX(Component.LEFT_ALIGNMENT);
			listPanel.add(instruction);
			listPanel.add(Box.createVerticalStrut(10));

			JCheckBox chkName = new JCheckBox("Employee Name");
			JCheckBox chkNumber = new JCheckBox("Employee Number");
			JCheckBox chkDept = new JCheckBox("Department");
			JCheckBox chkSalary = new JCheckBox("Monthly Salary");
			JCheckBox chkWorkDays = new JCheckBox("Work Days per Year");
			JCheckBox chkRegDays = new JCheckBox("Regular Days worked (this month)");
			JCheckBox chkRegHours = new JCheckBox("Regular Hours worked (this month)");
			JCheckBox chkSpecDays = new JCheckBox("Special Days worked (this month)");
			JCheckBox chkSpecHours = new JCheckBox("Special Hours worked (this month)");
			JCheckBox chkHolDays = new JCheckBox("Regular Holiday Days worked (this month)");
			JCheckBox chkHolHours = new JCheckBox("Regular Holiday Hours worked (this month)");

			JCheckBox[] checkboxes = {
					chkName, chkNumber, chkDept, chkSalary, chkWorkDays,
					chkRegDays, chkRegHours, chkSpecDays, chkSpecHours, chkHolDays, chkHolHours
			};

			for (JCheckBox cb : checkboxes) {
				cb.setFont(THEME_FONT_REGULAR);
				cb.setForeground(THEME_TEXT);
				cb.setBackground(Color.WHITE);
				cb.setFocusPainted(false);
				cb.setAlignmentX(Component.LEFT_ALIGNMENT);
				listPanel.add(cb);
				listPanel.add(Box.createVerticalStrut(4));
			}

			JButton btnOk = new JButton("OK");
			btnOk.setFocusPainted(false);
			btnOk.setFont(THEME_FONT_BOLD);
			btnOk.setBackground(THEME_SECONDARY); 
			btnOk.setForeground(THEME_TEXT);
			btnOk.setPreferredSize(new Dimension(80, 32));

			JButton btnCancel = new JButton("Cancel");
			btnCancel.setFocusPainted(false);
			btnCancel.setFont(THEME_FONT_BOLD);
			btnCancel.setBackground(Color.WHITE);
			btnCancel.setForeground(THEME_TEXT);
			btnCancel.setPreferredSize(new Dimension(80, 32));

			final boolean[] approved = {false};

			btnOk.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent ev) {
					approved[0] = true;
					checkboxDialog.dispose();
				}
			});
			btnCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent ev) {
					checkboxDialog.dispose();
				}
			});

			JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
			btnPanel.setBackground(THEME_BG);
			btnPanel.add(btnOk);
			btnPanel.add(btnCancel);

			JScrollPane checkScroll = new JScrollPane(listPanel);
			checkScroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
			checkboxDialog.add(checkScroll, BorderLayout.CENTER);
			checkboxDialog.add(btnPanel, BorderLayout.SOUTH);
			checkboxDialog.setVisible(true);

			if (!approved[0]) {
				return;
			}

			int selectedCount = 0;
			for (JCheckBox cb : checkboxes) {
				if (cb.isSelected()) selectedCount++;
			}

			if (selectedCount == 0) {
				JOptionPane.showMessageDialog(this, "No fields selected for update.");
				return;
			}

			JDialog inputDialog = new JDialog(this, "Enter New Values", true);
			inputDialog.setSize(440, Math.min(600, 120 + selectedCount * 65));
			inputDialog.setLocationRelativeTo(this);
			inputDialog.setLayout(new BorderLayout(10, 10));

			JPanel formPanel = new JPanel(new GridLayout(selectedCount, 2, 10, 15));
			formPanel.setBackground(Color.WHITE);
			formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

			java.util.Map<String, JTextField> fieldsMap = new java.util.HashMap<>();

			if (chkName.isSelected()) {
				JLabel lbl = new JLabel("Employee Name:"); lbl.setFont(THEME_FONT_BOLD); lbl.setForeground(THEME_TEXT);
				formPanel.add(lbl);
				JTextField tf = new JTextField(record.name); tf.setFont(THEME_FONT_REGULAR);
				formPanel.add(tf);
				fieldsMap.put("name", tf);
			}
			if (chkNumber.isSelected()) {
				JLabel lbl = new JLabel("Employee Number:"); lbl.setFont(THEME_FONT_BOLD); lbl.setForeground(THEME_TEXT);
				formPanel.add(lbl);
				JTextField tf = new JTextField(record.number); tf.setFont(THEME_FONT_REGULAR);
				formPanel.add(tf);
				fieldsMap.put("number", tf);
			}
			if (chkDept.isSelected()) {
				JLabel lbl = new JLabel("Department:"); lbl.setFont(THEME_FONT_BOLD); lbl.setForeground(THEME_TEXT);
				formPanel.add(lbl);
				JTextField tf = new JTextField(record.department); tf.setFont(THEME_FONT_REGULAR);
				formPanel.add(tf);
				fieldsMap.put("dept", tf);
			}
			if (chkSalary.isSelected()) {
				JLabel lbl = new JLabel("Monthly Salary:"); lbl.setFont(THEME_FONT_BOLD); lbl.setForeground(THEME_TEXT);
				formPanel.add(lbl);
				JTextField tf = new JTextField(String.valueOf(record.monthlyPay)); tf.setFont(THEME_FONT_REGULAR);
				formPanel.add(tf);
				fieldsMap.put("salary", tf);
			}
			if (chkWorkDays.isSelected()) {
				JLabel lbl = new JLabel("Work Days per Year:"); lbl.setFont(THEME_FONT_BOLD); lbl.setForeground(THEME_TEXT);
				formPanel.add(lbl);
				JTextField tf = new JTextField(String.valueOf(record.workingDaysYear)); tf.setFont(THEME_FONT_REGULAR);
				formPanel.add(tf);
				fieldsMap.put("workdays", tf);
			}
			if (chkRegDays.isSelected()) {
				JLabel lbl = new JLabel("Regular Days Worked:"); lbl.setFont(THEME_FONT_BOLD); lbl.setForeground(THEME_TEXT);
				formPanel.add(lbl);
				JTextField tf = new JTextField(String.valueOf(record.regularDays)); tf.setFont(THEME_FONT_REGULAR);
				formPanel.add(tf);
				fieldsMap.put("regdays", tf);
			}
			if (chkRegHours.isSelected()) {
				JLabel lbl = new JLabel("Regular Hours Worked:"); lbl.setFont(THEME_FONT_BOLD); lbl.setForeground(THEME_TEXT);
				formPanel.add(lbl);
				JTextField tf = new JTextField(String.valueOf(record.regularHours)); tf.setFont(THEME_FONT_REGULAR);
				formPanel.add(tf);
				fieldsMap.put("reghours", tf);
			}
			if (chkSpecDays.isSelected()) {
				JLabel lbl = new JLabel("Special Days Worked:"); lbl.setFont(THEME_FONT_BOLD); lbl.setForeground(THEME_TEXT);
				formPanel.add(lbl);
				JTextField tf = new JTextField(String.valueOf(record.specialDays)); tf.setFont(THEME_FONT_REGULAR);
				formPanel.add(tf);
				fieldsMap.put("specdays", tf);
			}
			if (chkSpecHours.isSelected()) {
				JLabel lbl = new JLabel("Special Hours Worked:"); lbl.setFont(THEME_FONT_BOLD); lbl.setForeground(THEME_TEXT);
				formPanel.add(lbl);
				JTextField tf = new JTextField(String.valueOf(record.specialHours)); tf.setFont(THEME_FONT_REGULAR);
				formPanel.add(tf);
				fieldsMap.put("spechours", tf);
			}
			if (chkHolDays.isSelected()) {
				JLabel lbl = new JLabel("Holiday Days Worked:"); lbl.setFont(THEME_FONT_BOLD); lbl.setForeground(THEME_TEXT);
				formPanel.add(lbl);
				JTextField tf = new JTextField(String.valueOf(record.holidayDays)); tf.setFont(THEME_FONT_REGULAR);
				formPanel.add(tf);
				fieldsMap.put("holdays", tf);
			}
			if (chkHolHours.isSelected()) {
				JLabel lbl = new JLabel("Holiday Hours Worked:"); lbl.setFont(THEME_FONT_BOLD); lbl.setForeground(THEME_TEXT);
				formPanel.add(lbl);
				JTextField tf = new JTextField(String.valueOf(record.holidayHours)); tf.setFont(THEME_FONT_REGULAR);
				formPanel.add(tf);
				fieldsMap.put("holhours", tf);
			}

			JButton btnSave = new JButton("Save Updates");
			btnSave.setFocusPainted(false);
			btnSave.setFont(THEME_FONT_BOLD);
			btnSave.setBackground(THEME_PRIMARY); 
			btnSave.setForeground(Color.WHITE);
			btnSave.setPreferredSize(new Dimension(130, 35));

			JButton btnCancelInput = new JButton("Cancel");
			btnCancelInput.setFocusPainted(false);
			btnCancelInput.setFont(THEME_FONT_BOLD);
			btnCancelInput.setBackground(Color.WHITE);
			btnCancelInput.setForeground(THEME_TEXT);
			btnCancelInput.setPreferredSize(new Dimension(90, 35));

			final boolean[] saved = {false};

			btnSave.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent ev) {
					saved[0] = true;
					inputDialog.dispose();
				}
			});
			btnCancelInput.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent ev) {
					inputDialog.dispose();
				}
			});

			JPanel inputBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
			inputBtnPanel.setBackground(THEME_BG);
			inputBtnPanel.add(btnSave);
			inputBtnPanel.add(btnCancelInput);

			JScrollPane formScroll = new JScrollPane(formPanel);
			formScroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
			inputDialog.add(formScroll, BorderLayout.CENTER);
			inputDialog.add(inputBtnPanel, BorderLayout.SOUTH);
			inputDialog.setVisible(true);

			if (!saved[0]) {
				return;
			}

			String oldEmployeeNumber = record.number;
			boolean calculationsChanged = false;

			if (chkName.isSelected()) {
				String val = fieldsMap.get("name").getText().trim();
				if (val.isEmpty()) throw new IllegalArgumentException("Employee name is required.");
				record.name = val;
			}
			if (chkNumber.isSelected()) {
				String val = fieldsMap.get("number").getText().trim();
				if (val.isEmpty()) throw new IllegalArgumentException("Employee number is required.");
				if (!val.equals(oldEmployeeNumber) && DatabaseIntegration.employeeExists(val)) {
					throw new IllegalArgumentException("Employee number " + val + " already exists.");
				}
				record.number = val;
			}
			if (chkDept.isSelected()) {
				String val = fieldsMap.get("dept").getText().trim();
				if (val.isEmpty()) throw new IllegalArgumentException("Department is required.");
				record.department = val;
			}
			if (chkSalary.isSelected()) {
				double val = parseDouble(fieldsMap.get("salary").getText(), "Monthly Salary");
				if (val != record.monthlyPay) {
					record.monthlyPay = val;
					calculationsChanged = true;
				}
			}
			if (chkWorkDays.isSelected()) {
				double val = parseDouble(fieldsMap.get("workdays").getText(), "Work Days per Year");
				if (val != record.workingDaysYear) {
					record.workingDaysYear = val;
					calculationsChanged = true;
				}
			}
			if (chkRegDays.isSelected()) {
				double val = parseDoubleOrZero(fieldsMap.get("regdays").getText(), "Regular Days Worked");
				if (val != record.regularDays) {
					record.regularDays = val;
					calculationsChanged = true;
				}
			}
			if (chkRegHours.isSelected()) {
				double val = parseDoubleOrZero(fieldsMap.get("reghours").getText(), "Regular Hours Worked");
				if (val != record.regularHours) {
					record.regularHours = val;
					calculationsChanged = true;
				}
			}
			if (chkSpecDays.isSelected()) {
				double val = parseDoubleOrZero(fieldsMap.get("specdays").getText(), "Special Days Worked");
				if (val != record.specialDays) {
					record.specialDays = val;
					calculationsChanged = true;
				}
			}
			if (chkSpecHours.isSelected()) {
				double val = parseDoubleOrZero(fieldsMap.get("spechours").getText(), "Special Hours Worked");
				if (val != record.specialHours) {
					record.specialHours = val;
					calculationsChanged = true;
				}
			}
			if (chkHolDays.isSelected()) {
				double val = parseDoubleOrZero(fieldsMap.get("holdays").getText(), "Holiday Days Worked");
				if (val != record.holidayDays) {
					record.holidayDays = val;
					calculationsChanged = true;
				}
			}
			if (chkHolHours.isSelected()) {
				double val = parseDoubleOrZero(fieldsMap.get("holhours").getText(), "Holiday Hours Worked");
				if (val != record.holidayHours) {
					record.holidayHours = val;
					calculationsChanged = true;
				}
			}

			if (calculationsChanged) {
				BasePayCalculator calculator = new BasePayCalculator(record.monthlyPay, record.workingDaysYear);
				record.dailyRate = calculator.getDailyRate();
				record.hourlyRate = calculator.getHourlyRate();

				double avgRegHours = record.regularHours / Math.max(1.0, record.regularDays);
				double avgSpecHours = record.specialHours / Math.max(1.0, record.specialDays);
				double avgHolHours = record.holidayHours / Math.max(1.0, record.holidayDays);

				double regularDayPay = record.regularDays * calculator.computeDailyEarnings(1, avgRegHours);
				double specialDayPay = record.specialDays * calculator.computeDailyEarnings(2, avgSpecHours);
				double holidayDayPay = record.holidayDays * calculator.computeDailyEarnings(3, avgHolHours);

				double regOtHours = record.regularDays * Math.max(0, avgRegHours - BasePayCalculator.STANDARD_HOURS);
				double specOtHours = record.specialDays * Math.max(0, avgSpecHours - BasePayCalculator.STANDARD_HOURS);
				double holOtHours = record.holidayDays * Math.max(0, avgHolHours - BasePayCalculator.STANDARD_HOURS);

				record.regularDayPay = regularDayPay;
				record.specialDayPay = specialDayPay;
				record.holidayDayPay = holidayDayPay;
				record.totalOvertime = regOtHours + specOtHours + holOtHours;

				record.totalHours = record.regularDays + record.specialDays + record.holidayDays; 
				record.totalBasePay = record.regularDayPay + record.specialDayPay + record.holidayDayPay;

				double[] deductions = Deductions.calculateAll(record.totalBasePay);
				record.sss = deductions[0];
				record.philhealth = deductions[1];
				record.pagibig = deductions[2];
				record.tax = deductions[3];
				record.totalDeductions = deductions[4];
				record.totalSalaryThisMonth = deductions[5];
			}

			boolean success = DatabaseIntegration.saveEmployeeRecord(record, oldEmployeeNumber);
			if (success) {
				JOptionPane.showMessageDialog(this, "Employee updated and payroll calculations synced successfully.");
			} else {
				JOptionPane.showMessageDialog(this, "Employee update failed.");
			}
		} catch (Exception ex) {
			Exceptions.handleUpdateError(this, ex);
		}
	}

	private void deleteEmployeeFlow() {
		try {
			String identifier = JOptionPane.showInputDialog(this, "Enter Employee Name or Number to delete:");
			if (identifier == null || identifier.trim().isEmpty()) {
				return;
			}
			String[] info = DatabaseIntegration.findEmployeeNameAndNumber(identifier.trim());
			if (info == null) {
				JOptionPane.showMessageDialog(this, "Employee does not exist.", "Delete Employee", JOptionPane.WARNING_MESSAGE);
				return;
			}
			int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete employee " + info[0] + " (" + info[1] + ")?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				boolean deleted = DatabaseIntegration.deleteEmployee(info[1]);
				if (deleted) {
					JOptionPane.showMessageDialog(this, "Employee record deleted successfully.");
				} else {
					JOptionPane.showMessageDialog(this, "Employee record was not deleted.");
				}
			} else {
				JOptionPane.showMessageDialog(this, "Deletion cancelled.");
			}
		} catch (SQLException ex) {
			Exceptions.handleDatabaseError(this, ex);
		}
	}

	private void loadRecords() {
		try {
			List<String[]> rows = DatabaseIntegration.getEmployeeSummaryRows();
			tableModel.setRowCount(0);
			for (String[] row : rows) {
				tableModel.addRow(row);
			}
		} catch (SQLException ex) {
			Exceptions.handleDatabaseError(this, ex);
		}
	}

	private double parseDouble(String value, String fieldName) {
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException(fieldName + " is required.");
		}
		try {
			return Double.parseDouble(value.trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Please enter a valid number for " + fieldName + ".");
		}
	}

	private JPanel createSummaryField(String label, String value) {
		JPanel panel = new JPanel(new BorderLayout(8, 0));
		panel.setOpaque(false); 

		JLabel lbl = new JLabel(label);
		lbl.setFont(THEME_FONT_REGULAR);
		lbl.setForeground(THEME_TEXT);

		JTextField tf = new JTextField(value);
		tf.setEditable(false);
		tf.setBackground(new Color(248, 250, 252)); 
		tf.setForeground(THEME_TEXT);
		tf.setFont(THEME_FONT_BOLD);
		tf.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
				BorderFactory.createEmptyBorder(4, 6, 4, 6)
				));

		panel.add(lbl, BorderLayout.WEST);
		panel.add(tf, BorderLayout.CENTER);
		return panel;
	}

	private double parseDoubleOrZero(String value, String fieldName) {
		if (value == null || value.trim().isEmpty()) {
			return 0.0;
		}
		try {
			return Double.parseDouble(value.trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Please enter a valid number for " + fieldName + ".");
		}
	}
}
