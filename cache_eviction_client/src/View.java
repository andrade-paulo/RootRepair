import modelo.DAO.KMP;
import modelo.DAO.LogDAO;
import modelo.entities.OrdemServico;
import modelo.entities.Usuario;


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

    private static void buscarOS() {
        limparTela();
        
        System.out.print("Código da Ordem de Serviço: ");
        int codigo = Client.scanner.nextInt();
        Client.scanner.nextLine();

        try {
            OrdemServico ordemServico = Controller.getOrdemServico(codigo);
            System.out.println("\n" + ordemServico + "\n---------------------------------");
        } catch (Exception e) {
            System.out.println(Color.RED + "Oops! Ordem de Serviço não encontrada." + Color.RESET);
        }
    }

    private static void removerOS() {
        limparTela();
        
        System.out.print("Código da Ordem de Serviço: ");
        int codigo = Client.scanner.nextInt();

        try {
            Controller.removerOrdemServico(codigo);
            System.out.println(Color.GREEN + "Ordem de Serviço " + codigo + " removida com sucesso!" + Color.RESET);
        } catch (Exception e) {
            System.out.println(Color.RED + "Oops! Ordem de Serviço não encontrada." + Color.RESET);
        }
    }

    private static void atualizarOS() {
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
            System.out.println(Color.RED + "Oops! Ordem de Serviço não encontrada." + Color.RESET);
        }
    }

    private static void listarMeusOS() {
        limparTela();
        
        try {
            OrdemServico[] meusOS = Controller.getOrdensByUsuario(usuario);
            System.out.println(Color.CYAN + "Minhas Ordens de Serviço:" + Color.RESET);
            for (OrdemServico os : meusOS) {
                System.out.println(os + "\n");
            }
        } catch (Exception e) {
            System.out.println(Color.RED + "Oops! Nenhuma Ordem de Serviço encontrada." + Color.RESET);
        }
    }

    private static void listarTodosOS() {
        limparTela();

        try {
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

        } catch (Exception e) {
            System.out.println(Color.RED + "Oops! Nenhuma Ordem de Serviço encontrada." + Color.RESET);
            return;
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
}
