package gui;

import misc.IPv4Validator;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

import server.ServerState;
import server.Server;

public class GUI {

	protected Shell shell;
	private TabFolder tabFolder;
	private TabItem tbtmServer;
	private TabItem tbtmClient;
	private Composite composite;
	private Composite composite_1;
	private Label lblIp;
	private Text text;
	private Spinner spinnerPort;
	private Label lblPort;
	private Button btnConnect;
	private StyledText clientTextBox;
	private TextViewer textViewer;
	private Button btnNewJoke;
	private Label lblListeningPort;
	private Spinner spinner;
	private Button btnStartServer;
	private Button btnStopAccepting;
	private Text textNewJoke;
	private Label lblNewJoke;
	private Button btnSaveJoke;
	private ServerState serverState = ServerState.getInstance();
	private Thread serverThread = null;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			GUI window = new GUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		initGUI();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void initGUI() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		tabFolder = new TabFolder(shell, SWT.NONE);

		tbtmServer = new TabItem(tabFolder, SWT.NONE);
		tbtmServer.setText("Server");

		composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmServer.setControl(composite_1);
		composite_1.setLayout(new GridLayout(4, false));

		lblListeningPort = new Label(composite_1, SWT.NONE);
		lblListeningPort.setText("Listening port");

		spinner = new Spinner(composite_1, SWT.BORDER);
		spinner.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				serverState.setListeningPort(spinner.getSelection());
			}
		});
		spinner.setMaximum(65535);
		spinner.setMinimum(1024);
		spinner.setSelection(27015);

		btnStartServer = new Button(composite_1, SWT.NONE);
		btnStartServer.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (!serverState.running()) {
					serverState.startServer();
					btnStartServer.setText("Stop server");
					serverThread = new Thread(new Server());
					serverThread.start();
				} else {
					btnStartServer.setEnabled(false);
					btnStartServer.setText("Wait");
					serverState.stopServer();
					try {
						serverThread.join();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					serverThread = null;
					btnStartServer.setText("Start server");
					btnStartServer.setEnabled(true);
				}
			}
		});
		btnStartServer.setText("Start server");

		btnStopAccepting = new Button(composite_1, SWT.NONE);
		btnStopAccepting.setEnabled(false);
		btnStopAccepting.setText("Stop accepting");

		lblNewJoke = new Label(composite_1, SWT.NONE);
		lblNewJoke.setText("New joke");
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);

		textNewJoke = new Text(composite_1, SWT.BORDER | SWT.WRAP);
		textNewJoke.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				btnSaveJoke.setEnabled(textNewJoke.getText().length() > 10);
			}
		});
		GridData gd_textNewJoke = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 4, 1);
		gd_textNewJoke.widthHint = 209;
		gd_textNewJoke.heightHint = 94;
		textNewJoke.setLayoutData(gd_textNewJoke);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);

		btnSaveJoke = new Button(composite_1, SWT.NONE);
		btnSaveJoke.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				serverState.addJoke(textNewJoke.getText());
				textNewJoke.setText("");
			}
		});
		btnSaveJoke.setEnabled(false);
		btnSaveJoke.setText("Save joke");
		new Label(composite_1, SWT.NONE);

		tbtmClient = new TabItem(tabFolder, SWT.NONE);
		tbtmClient.setText("Client");

		composite = new Composite(tabFolder, SWT.NONE);
		tbtmClient.setControl(composite);
		composite.setLayout(new GridLayout(6, false));

		lblIp = new Label(composite, SWT.NONE);
		lblIp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblIp.setText("IP");
		new Label(composite, SWT.NONE);

		text = new Text(composite, SWT.BORDER | SWT.RIGHT);
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				btnConnect.setEnabled(IPv4Validator.isValidIPv4(text.getText()));
			}
		});
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		lblPort = new Label(composite, SWT.NONE);
		lblPort.setText("Port");

		spinnerPort = new Spinner(composite, SWT.BORDER);
		spinnerPort.setMaximum(65535);
		spinnerPort.setMinimum(1024);
		spinnerPort.setSelection(27015);

		btnConnect = new Button(composite, SWT.NONE);
		btnConnect.setEnabled(false);
		btnConnect.setText("Connect");

		textViewer = new TextViewer(composite, SWT.BORDER);
		clientTextBox = textViewer.getTextWidget();
		clientTextBox.setEditable(false);
		clientTextBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 6, 1));
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);

		btnNewJoke = new Button(composite, SWT.NONE);
		GridData gd_btnNewJoke = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 2, 1);
		gd_btnNewJoke.widthHint = 57;
		btnNewJoke.setLayoutData(gd_btnNewJoke);
		btnNewJoke.setEnabled(false);
		btnNewJoke.setText("Request joke");
	}
}
