import java.util.Arrays;
import java.util.List;

import modelo.DAO.KMP;
import modelo.DAO.LogDAO;
import shared.entities.OrdemServico;
import shared.entities.Usuario;


public class View {
    private static Usuario usuario;
    
    public View() {}

    public static boolean realizarLogin() {
        System.out.println(Color.CYAN + "Bem-vindo ao sistema de Ordem de Serviço!" + Color.RESET);

        // Check if there's something in the buffer
        if (Client.scanner.hasNextLine()) {
            Client.scanner.nextLine();
        }

        System.out.print("Digite seu nome: ");
        String nome = Client.scanner.nextLine();
        System.out.print("Digite seu CPF (apenas números): ");
        String cpf = Client.scanner.nextLine();
        
        if (nome.isEmpty() || cpf.isEmpty()) {
            limparTela();
            System.out.println(Color.RED + "Oops! Os dois campos devem ser preenchidos." + Color.RESET);
            return realizarLogin();
        }

        try {
            usuario = Controller.getUsuario(cpf);
            
            if (!usuario.getNome().equals(nome)) {
                limparTela();
                System.out.println(Color.RED + "Oops! As credenciais informadas não conferem." + Color.RESET);
                return realizarLogin();
            }

        } catch (Exception e) {
            System.out.print("Usuario não encontrado. Deseja se cadastrar? (s/n): ");

            String resposta = Client.scanner.nextLine();
            
            if (resposta.equals("s") || resposta.equals("S")) {
                usuario = new Usuario(nome, cpf);
                try {
                    Controller.addUsuario(usuario);
                } catch (Exception ex) {
                    System.out.println(Color.RED + "Oops! Erro ao cadastrar usuário." + Color.RESET);
                    return false;
                }
                
                System.out.println(Color.GREEN + "Usuario cadastrado com sucesso!" + Color.RESET);
            } else {
                System.out.println("Saindo do programa...");
                System.exit(0);
                return false;
            }
        }

        return true;
    }

    public static void showMenu() throws Exception {
        limparTela();
        String escolha = "";
        
        do {
            System.out.println(Color.CYAN + "Menu principal - " + usuario.getNome() + Color.RESET);
            System.out.println("1. Cadastrar Ordem de Serviço");
            System.out.println("2. Listar minhas Ordens de Serviço");
            System.out.println("3. Listar todas as Ordens de Serviço");
            System.out.println("4. Buscar Ordem de Serviço");
            System.out.println("5. Remover Ordem de Serviço");
            System.out.println("6. Atualizar Ordem de Serviço");
            System.out.println("7. Logout");
            System.out.println("8. Buscar no log");
            System.out.println("9. Popular banco de dados");
            System.out.println("0. Sair do sistema");
            System.out.print("Escolha uma opção: ");
            
            escolha = Client.scanner.nextLine();
            
            switch (escolha) {
                case "1":
                    cadastrarOS();
                    break;
                case "2":
                    listarMeusOS();
                    break;
                case "3":
                    listarTodosOS();
                    break;
                case "4":
                    buscarOS();
                    break;
                case "5":
                    removerOS();
                    break;
                case "6":
                    atualizarOS();
                    break;
                case "7":
                    usuario = null;
                    realizarLogin();
                    break;
                case "8":
                    buscarLog();
                    break;
                case "9":
                    populateDatabase();
                    break;
                case "0":
                    System.out.println("Saindo do sistema...");
                    escolha = "0";
                    break;
                default:
                    System.out.println(Color.RED + "Opção inválida. Tente novamente." + Color.RESET);
                    break;
            }
            
            System.out.println();
        } while (escolha != "0");
    }

    private static void cadastrarOS() throws Exception {
        limparTela();

        System.out.print("Título do Serviço: ");
        String titulo = Client.scanner.nextLine();

        System.out.print("Descrição do Serviço: ");
        String descricao = Client.scanner.nextLine();

        OrdemServico ordemServico = new OrdemServico(titulo, descricao, usuario);
        Controller.addOrdemServico(ordemServico);

        System.out.println(Color.GREEN + "Ordem de Serviço cadastrada com sucesso!" + Color.RESET);
    }

    private static void buscarOS() throws Exception {
        limparTela();
        
        System.out.print("Código da Ordem de Serviço: ");
        int codigo = Client.scanner.nextInt();
        Client.scanner.nextLine();

        try {
            OrdemServico ordemServico = Controller.getOrdemServico(codigo);
            System.out.println("\n" + ordemServico + "\n---------------------------------");
        } catch (Exception e) {
            if (e.getMessage().equals("Ordem de Servico nao encontrada")) {
                System.out.println(Color.RED + "Oops! Ordem de Serviço não encontrada." + Color.RESET);
            } else {
                throw e;
            }
        }
    }

    private static void removerOS() throws Exception {
        limparTela();
        
        System.out.print("Código da Ordem de Serviço: ");
        int codigo = Client.scanner.nextInt();
        Client.scanner.nextLine();

        try {
            Controller.removerOrdemServico(codigo);
            System.out.println(Color.GREEN + "Ordem de Serviço " + codigo + " removida com sucesso!" + Color.RESET);
        } catch (Exception e) {
            if (e.getMessage().equals("Ordem de Servico nao encontrada")) {
                System.out.println(Color.RED + "Oops! Ordem de Serviço não encontrada." + Color.RESET);
            } else {
                throw e;
            }
        }
    }

    private static void atualizarOS() throws Exception {
        limparTela();
        
        System.out.print("Código da Ordem de Serviço: ");
        int codigo = Client.scanner.nextInt();
        Client.scanner.nextLine();

        try {
            OrdemServico ordemServico = Controller.getOrdemServico(codigo);
            System.out.println("Deixe em branco para manter o valor atual.");

            System.out.print("Título do Serviço: ");
            String titulo = Client.scanner.nextLine();

            System.out.print("Descrição do Serviço: ");
            String descricao = Client.scanner.nextLine();
            
            if (!titulo.isEmpty()) {
                ordemServico.setTitulo(titulo);
            }

            if (!descricao.isEmpty()) {
                ordemServico.setDescricao(descricao);
            }

            Controller.updateOrdemServico(ordemServico);
            System.out.println(Color.GREEN + "Ordem de Serviço atualizada com sucesso!" + Color.RESET);
        } catch (Exception e) {
            if (e.getMessage().equals("Ordem de Servico nao encontrada")) {
                System.out.println(Color.RED + "Oops! Ordem de Serviço não encontrada." + Color.RESET);
            } else {
                throw e;
            }
        }
    }

    private static void listarMeusOS() throws Exception {
        limparTela();
        
        OrdemServico[] meusOS = Controller.getOrdensByUsuario(usuario);
        System.out.println(Color.CYAN + "Minhas Ordens de Serviço:" + Color.RESET);
        for (OrdemServico os : meusOS) {
            System.out.println(os + "\n");
        }
    }

    private static void listarTodosOS() throws Exception {
        limparTela();

        OrdemServico[] todasOS = Controller.getAllOrdemServicos();

        if (todasOS.length == 0) {
            System.out.println(Color.RED + "Oops! Nenhuma Ordem de Serviço encontrada." + Color.RESET);
            return;
        } else if(todasOS.length == 1) {
            System.out.println(Color.CYAN + "1 Ordem de Serviço encontrada:" + Color.RESET);
        } else {
            System.out.println(Color.CYAN + todasOS.length + " Ordens de Serviço encontradas:" + Color.RESET);
        }

        for (OrdemServico os : todasOS) {
            System.out.println(os + "\n");
        }
    }
    

    private static void buscarLog() {
        limparTela();
        
        System.out.print("Digite a sua busca: ");
        String palavra = Client.scanner.nextLine();

        try {
            System.out.println("\nResultados da busca:");
            
            int[] lines = KMP.searchLog(palavra);
            for (int i = 0; i < lines.length; i++) {
                if (lines[i] != 0) {
                    System.out.println(LogDAO.getLine(i));
                }
            }
        } catch (Exception e) {
            System.out.println(Color.RED + "Oops! Busca não encontrada." + Color.RESET);
        }
    }


    public static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush(); 
    }


    public static void populateDatabase() {
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
            OrdemServico os = new OrdemServico(ordensDeServico.get(i), descricoes.get(i), usuario);
            try {
                Controller.addOrdemServico(os);
            } catch (Exception e) {
                System.out.println(Color.RED + "Erro ao adicionar Ordem de Serviço: " + e.getMessage() + Color.RESET);
            }
        }

        System.out.println(Color.GREEN + "Banco de dados populado com sucesso!" + Color.RESET);
    }
}
