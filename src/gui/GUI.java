/*
 * Juan Antonio Aldea Armenteros (0404450)
 * LUT - Object Oriented Programming Techniques
 *                  2013
 */

package gui;

import java.io.IOException;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import misc.IPv4Validator;
import server.ServerState;
import client.ClientState;
import server.Server;
import client.Client;

public class GUI {

    protected Shell     shell;
    private TabFolder   tabFolder;
    private TabItem     tbtmServer;
    private TabItem     tbtmClient;
    private Composite   composite;
    private Composite   composite_1;
    private Label       lblIp;
    private Text        textServerIP;
    private Spinner     spinnerPort;
    private Label       lblPort;
    private Button      btnConnect;
    private Button      btnRequestJoke;
    private Label       lblListeningPort;
    private Spinner     spinnerListeningPort;
    private Button      btnStartServer;
    private Text        textNewJoke;
    private Label       lblNewJoke;
    private Button      btnSaveJoke;
    private ServerState serverState  = ServerState.getInstance();
    private ClientState clientState  = ClientState.getInstance();
    private Thread      serverThread = null;
    private Server      server       = new Server();
    private Client      client       = null;
    private Thread      clientThread = null;
    private Button      btnAcceptingClients;
    private Text        textClient2;

    public GUI() {
        client = new Client(this);
    }

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

        serverState.stopServer();

        if (server != null) {
            server.wakeUp();
        }

        if (serverThread != null) {
            try {
                serverThread.join();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        clientState.stopClient();
        if (client != null) {
            client.wakeUp();
        }
        if (clientThread != null) {
            try {
                clientThread.join();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
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

        spinnerListeningPort = new Spinner(composite_1, SWT.BORDER);
        spinnerListeningPort.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                serverState.setListeningPort(spinnerListeningPort.getSelection());
            }
        });
        spinnerListeningPort.setMaximum(65535);
        spinnerListeningPort.setMinimum(1024);
        spinnerListeningPort.setSelection(27015);

        btnStartServer = new Button(composite_1, SWT.NONE);
        btnStartServer.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!serverState.isRunning()) {
                    setGUIServerConnectedState();
                    serverState.startServer();
                    serverThread = new Thread(server);
                    serverThread.start();

                } else {
                    setGUIServerDisconnectingState();
                    serverState.stopServer();
                    server.wakeUp();
                    try {
                        serverThread.join();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    serverThread = null;
                    setGUIServerDisconnectedState();
                }
            }
        });
        btnStartServer.setText("Start server");

        btnAcceptingClients = new Button(composite_1, SWT.CHECK);
        btnAcceptingClients.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                serverState.setAcceptingNewConnections(btnAcceptingClients.getSelection());
            }
        });
        btnAcceptingClients.setSelection(true);
        btnAcceptingClients.setText("Accepting clients");

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
        GridData gd_textNewJoke = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
        gd_textNewJoke.widthHint = 209;
        gd_textNewJoke.heightHint = 94;
        textNewJoke.setLayoutData(gd_textNewJoke);
        new Label(composite_1, SWT.NONE);
        new Label(composite_1, SWT.NONE);

        btnSaveJoke = new Button(composite_1, SWT.NONE);
        btnSaveJoke.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnSaveJoke.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
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
        composite.setLayout(new GridLayout(5, false));

        lblIp = new Label(composite, SWT.NONE);
        lblIp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblIp.setText("IP");

        textServerIP = new Text(composite, SWT.BORDER | SWT.RIGHT);
        textServerIP.setText("127.0.0.1");
        textServerIP.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                btnConnect.setEnabled(IPv4Validator.isValidIPv4(textServerIP.getText()));
            }
        });
        textServerIP.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        lblPort = new Label(composite, SWT.NONE);
        lblPort.setText("Port");

        spinnerPort = new Spinner(composite, SWT.BORDER);
        spinnerPort.setMaximum(65535);
        spinnerPort.setMinimum(1024);
        spinnerPort.setSelection(27015);

        btnConnect = new Button(composite, SWT.NONE);
        btnConnect.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!clientState.isRunning()) {
                    disableServerTab();
                    setGUIClientConnectedState();
                    clientState.setServerAddress(textServerIP.getText(), spinnerPort.getSelection());
                    clientState.startClient();
                    clientThread = new Thread(client);
                    clientThread.start();
                } else {
                    enableServerTab();
                    clientState.stopClient();
                    client.wakeUp();
                    try {
                        clientThread.join();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    clientThread = null;
                    setGUIClientDisconnectedState();
                }
            }
        });
        btnConnect.setText("Connect");
        new Label(composite, SWT.NONE);

        textClient2 = new Text(composite, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP);
        GridData gd_textClient2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
        gd_textClient2.widthHint = 275;
        gd_textClient2.heightHint = 163;
        textClient2.setLayoutData(gd_textClient2);
        new Label(composite, SWT.NONE);
        new Label(composite, SWT.NONE);
        new Label(composite, SWT.NONE);

        btnRequestJoke = new Button(composite, SWT.NONE);
        btnRequestJoke.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    client.requestJoke();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        GridData gd_btnRequestJoke = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
        gd_btnRequestJoke.widthHint = 57;
        btnRequestJoke.setLayoutData(gd_btnRequestJoke);
        btnRequestJoke.setEnabled(false);
        btnRequestJoke.setText("Request joke");
    }

    public void printJoke(final String joke) {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                textClient2.setText(joke);
            }
        });
    }

    protected void enableServerTab() {
        tabFolder.getTabList()[0].setEnabled(true);
        spinnerListeningPort.setEnabled(true);
        btnStartServer.setEnabled(true);
        btnAcceptingClients.setEnabled(true);
        textNewJoke.setEnabled(true);
    }

    protected void disableServerTab() {
        tabFolder.getTabList()[0].setEnabled(false);
        spinnerListeningPort.setEnabled(false);
        btnStartServer.setEnabled(false);
        btnAcceptingClients.setEnabled(false);
        textNewJoke.setEnabled(false);
    }

    protected void enableClientTab() {
        tabFolder.getTabList()[1].setEnabled(true);
        textServerIP.setEnabled(true);
        spinnerPort.setEnabled(true);
        btnConnect.setEnabled(true);
    }

    protected void disableClientTab() {
        tabFolder.getTabList()[1].setEnabled(false);
        textServerIP.setEnabled(false);
        spinnerPort.setEnabled(false);
        btnConnect.setEnabled(false);
    }

    protected void setGUIServerConnectedState() {
        disableClientTab();
        btnStartServer.setText("Stop server");
        spinnerListeningPort.setEnabled(false);
    }

    protected void setGUIServerDisconnectingState() {
        btnStartServer.setEnabled(false);
        btnStartServer.setText("Wait");
    }

    protected void setGUIServerDisconnectedState() {
        btnStartServer.setText("Start server");
        btnStartServer.setEnabled(true);
        spinnerListeningPort.setEnabled(true);
        enableClientTab();
    }

    protected void setGUIClientConnectedState() {
        btnRequestJoke.setEnabled(true);
        btnConnect.setText("Disconnect");
        spinnerPort.setEnabled(false);
        textServerIP.setEnabled(false);
        disableServerTab();
    }

    protected void setGUIClientDisconnectedState() {
        btnRequestJoke.setEnabled(false);
        btnConnect.setText("Connect");
        spinnerPort.setEnabled(true);
        textServerIP.setEnabled(true);
        enableServerTab();
    }

    public void requestSetGUIClientDisconnectedState() {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                setGUIClientDisconnectedState();
            }
        });
    }

    public void requestSetGUIClientConnectedState() {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                setGUIClientConnectedState();
            }
        });
    }

    public void requestClientPrint(final String msg) {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                textClient2.setText(msg);
            }
        });
    }

    public void requestClientClear() {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                textClient2.setText("");
            }
        });
    }
}
