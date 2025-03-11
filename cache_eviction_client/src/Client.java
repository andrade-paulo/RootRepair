import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.io.IOException;

import model.DAO.LogDAO;

public class Client {
    private static final String locationAdress = "localhost";
    private static final int locationServerPort = 5000;


    public static void main(String[] args) throws Exception {
        LogDAO.loadLog();

        // Connect to location server and get the proxy adress and port
        String proxyAdress = "";
        int proxyPort = 0;

        try (Socket locationSocket = new Socket(locationAdress, locationServerPort);
             DataInputStream locationIn = new DataInputStream(locationSocket.getInputStream());
             DataOutputStream locationOut = new DataOutputStream(locationSocket.getOutputStream())) {

            locationOut.writeUTF("eW91IHNoYWxsIG5vdCBwYXNz");  // Send a key as request to the location server
            proxyAdress = locationIn.readUTF();  // Read the proxy adress
            proxyPort = locationIn.readInt();  // Read the proxy port

            // Log the proxy adress and port
            LogDAO.addLog("[LOCATION SERVER] Proxy running on " + proxyAdress + ":" + proxyPort);
        } catch (IOException e) {
            System.out.println("Error. Could not connect to the location server.");
            e.printStackTrace();
        }

        // Initiate the system for the user
        if (proxyPort != 0 && proxyAdress != "") {
            try(Socket proxySocket = new Socket(proxyAdress, proxyPort);
            ObjectOutputStream proxyOut = new ObjectOutputStream(proxySocket.getOutputStream());
            ObjectInputStream proxyIn = new ObjectInputStream(proxySocket.getInputStream())) {
     
                // Set the controller
                Controller.setProxyServer(proxyOut, proxyIn);

                // Start the user interface
                View.limparTela();
                if (View.realizarLogin()) {
                    View.showMenu();
                } else {
                    System.out.println("\nVolte sempre!");
                }
            } catch (IOException e) {
                System.out.println("Error. Could not connect to the proxy server.");
                e.printStackTrace();
            }
        }
    }

    
    /*@SuppressWarnings("unused")
    private static void popularDatabase() throws Exception {
        OrdemServicoDAO ordemServicoDAO = new OrdemServicoDAO();
        Usuario chatGPT = new Usuario("ChatGPT", "12345678900");

        List<String> ordensDeServico = Arrays.asList(
            "Troca de webcam",
            "Manutenção de computador",
            "Troca de tela",
            "Instalação de software",
            "Limpeza interna de computador",
            "Substituição de teclado",
            "Atualização de sistema operacional",
            "Configuração de rede",
            "Reparação de impressora",
            "Troca de bateria de notebook",
            "Recuperação de dados",
            "Formatação de computador",
            "Troca de HD",
            "Instalação de antivírus",
            "Configuração de e-mail",
            "Substituição de memória RAM",
            "Reparo de cabo de alimentação",
            "Instalação de câmera de segurança",
            "Configuração de roteador",
            "Instalação de sistema de backup",
            "Substituição de placa-mãe",
            "Instalação de SSD",
            "Troca de fonte de alimentação",
            "Configuração de servidor",
            "Reparo de conector USB",
            "Instalação de monitor",
            "Troca de ventoinha de resfriamento",
            "Configuração de impressora",
            "Reparo de placa de vídeo",
            "Instalação de software de monitoramento",
            "Troca de mouse",
            "Configuração de firewall",
            "Instalação de software de edição",
            "Substituição de disco rígido externo",
            "Configuração de VPN",
            "Reparo de alto-falante",
            "Instalação de scanner",
            "Atualização de drivers",
            "Troca de cabo HDMI",
            "Configuração de backup automático",
            "Instalação de sistema de telefonia VoIP",
            "Reparo de leitor de cartão",
            "Substituição de processador",
            "Configuração de armazenamento em nuvem",
            "Instalação de painel de controle remoto",
            "Reparo de dispositivo móvel",
            "Instalação de software de segurança",
            "Configuração de contas de usuário",
            "Substituição de cooler",
            "Reparo de tela de notebook",
            "Instalação de rede sem fio",
            "Configuração de autenticação multifator",
            "Reparo de teclado de notebook",
            "Troca de memória flash",
            "Instalação de sistema de videoconferência",
            "Reparo de conector de energia",
            "Configuração de ambiente virtual",
            "Troca de gabinete de computador",
            "Reparo de sistema de som",
            "Instalação de software de automação",
            "Troca de webcam",
            "Manutenção de computador",
            "Troca de tela",
            "Instalação de software",
            "Limpeza interna de computador",
            "Substituição de teclado",
            "Atualização de sistema operacional",
            "Configuração de rede",
            "Reparação de impressora",
            "Troca de bateria de notebook",
            "Recuperação de dados",
            "Formatação de computador",
            "Troca de HD",
            "Instalação de antivírus",
            "Configuração de e-mail",
            "Substituição de memória RAM",
            "Reparo de cabo de alimentação",
            "Instalação de câmera de segurança",
            "Configuração de roteador",
            "Instalação de sistema de backup"
        );

        List<String> descricoes = Arrays.asList(
            "Substituição de webcam com defeito por uma nova e funcional.",
            "Diagnóstico e resolução de problemas de hardware e software em computadores.",
            "Substituição de tela danificada ou defeituosa de dispositivos eletrônicos.",
            "Instalação de novos programas e aplicativos no computador do usuário.",
            "Remoção de poeira e sujeira do interior do computador para evitar superaquecimento.",
            "Substituição de teclados defeituosos em notebooks e desktops.",
            "Atualização do sistema operacional para a versão mais recente e estável.",
            "Configuração de redes locais ou sem fio para garantir conectividade eficiente.",
            "Reparação de problemas de hardware e software em impressoras de diferentes modelos.",
            "Troca da bateria interna de notebooks para prolongar o tempo de uso.",
            "Recuperação de arquivos perdidos ou excluídos acidentalmente em dispositivos de armazenamento.",
            "Formatação completa do disco rígido e reinstalação do sistema operacional.",
            "Substituição do disco rígido defeituoso por um novo ou upgrade para maior capacidade.",
            "Instalação e configuração de software antivírus para proteção contra malware.",
            "Configuração de contas de e-mail e sincronização com dispositivos móveis.",
            "Instalação de novos módulos de memória RAM para melhorar o desempenho do sistema.",
            "Reparação de cabos de alimentação danificados ou com mau contato.",
            "Instalação de sistemas de câmeras de segurança para monitoramento em tempo real.",
            "Configuração de roteadores para fornecer acesso à internet e criar redes Wi-Fi.",
            "Implementação de soluções de backup para proteção de dados importantes.",
            "Substituição de placas-mãe defeituosas para restaurar o funcionamento do computador.",
            "Instalação de SSD para aumentar a velocidade e desempenho do sistema.",
            "Troca de fontes de alimentação defeituosas ou com falha para garantir o funcionamento estável do PC.",
            "Configuração de servidores para ambientes de rede e hospedagem de aplicativos.",
            "Reparo de conectores USB danificados ou com mau funcionamento.",
            "Instalação e configuração de monitores novos, incluindo ajuste de resolução.",
            "Substituição de ventoinhas de resfriamento para evitar superaquecimento de componentes.",
            "Configuração de impressoras em rede e instalação de drivers apropriados.",
            "Reparo de placas de vídeo com falhas de exibição ou desempenho reduzido.",
            "Instalação de software de monitoramento para supervisão de atividades e desempenho.",
            "Troca de mouses defeituosos ou atualização para modelos mais ergonômicos.",
            "Configuração de firewalls para proteção de redes contra acessos não autorizados.",
            "Instalação de software de edição de vídeo, imagem ou áudio para produção de conteúdo.",
            "Substituição de discos rígidos externos danificados ou atualização para maior capacidade.",
            "Configuração de redes VPN para acesso remoto seguro a redes corporativas.",
            "Reparo de alto-falantes com problemas de som ou mau contato.",
            "Instalação e configuração de scanners para digitalização de documentos.",
            "Atualização de drivers de hardware para garantir compatibilidade e desempenho.",
            "Substituição de cabos HDMI defeituosos ou com falhas na transmissão de vídeo.",
            "Configuração de sistemas de backup automático para proteção contínua de dados.",
            "Instalação de sistemas de telefonia VoIP para comunicação de voz via internet.",
            "Reparo de leitores de cartão com falhas na leitura ou conexão.",
            "Substituição de processadores para melhorar o desempenho ou reparar falhas.",
            "Configuração de soluções de armazenamento em nuvem para acesso remoto a arquivos.",
            "Instalação de painéis de controle remoto para gestão de sistemas à distância.",
            "Reparo de dispositivos móveis como smartphones e tablets com problemas de hardware.",
            "Instalação de software de segurança para proteger dados sensíveis e privacidade.",
            "Configuração de contas de usuário e permissões de acesso em sistemas operacionais.",
            "Substituição de coolers defeituosos para evitar superaquecimento de componentes.",
            "Reparo de telas de notebooks danificadas ou com problemas de exibição.",
            "Instalação e configuração de redes sem fio para acesso à internet.",
            "Configuração de autenticação multifator para aumentar a segurança do login.",
            "Reparo de teclados de notebook com teclas defeituosas ou mau contato.",
            "Substituição de memória flash danificada ou atualização para maior capacidade.",
            "Instalação de sistemas de videoconferência para reuniões remotas.",
            "Reparo de conectores de energia com mau contato ou falha de alimentação.",
            "Configuração de ambientes virtuais para testes e desenvolvimento de software.",
            "Substituição de gabinetes de computador para maior espaço ou melhor ventilação.",
            "Reparo de sistemas de som com problemas de áudio ou falha nos componentes.",
            "Instalação de software de automação para otimização de processos.",
            "Substituição de webcam com defeito por uma nova e funcional.",
            "Diagnóstico e resolução de problemas de hardware e software em computadores.",
            "Substituição de tela danificada ou defeituosa de dispositivos eletrônicos.",
            "Instalação de novos programas e aplicativos no computador do usuário.",
            "Remoção de poeira e sujeira do interior do computador para evitar superaquecimento.",
            "Substituição de teclados defeituosos em notebooks e desktops.",
            "Atualização do sistema operacional para a versão mais recente e estável.",
            "Configuração de redes locais ou sem fio para garantir conectividade eficiente.",
            "Reparação de impressoras de diferentes modelos com problemas de hardware e software.",
            "Troca da bateria interna de notebooks para prolongar o tempo de uso.",
            "Recuperação de arquivos perdidos ou excluídos acidentalmente em dispositivos de armazenamento.",
            "Formatação completa do disco rígido e reinstalação do sistema operacional.",
            "Substituição do disco rígido defeituoso por um novo ou upgrade para maior capacidade.",
            "Instalação e configuração de software antivírus para proteção contra malware.",
            "Configuração de contas de e-mail e sincronização com dispositivos móveis.",
            "Instalação de novos módulos de memória RAM para melhorar o desempenho do sistema.",
            "Reparação de cabos de alimentação danificados ou com mau contato.",
            "Instalação de sistemas de câmeras de segurança para monitoramento em tempo real.",
            "Configuração de roteadores para fornecer acesso à internet e criar redes Wi-Fi.",
            "Implementação de soluções de backup para proteção de dados importantes."
        );

        for (int i = 0; i < ordensDeServico.size(); i++) {
            OrdemServico ordemServico = new OrdemServico(ordensDeServico.get(i), descricoes.get(i), chatGPT);
            ordemServicoDAO.addOrdemServico(ordemServico);
        }
    }*/
}
