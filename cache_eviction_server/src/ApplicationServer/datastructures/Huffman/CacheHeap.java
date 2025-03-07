package datastructures;

import java.io.Serializable;

import model.entities.OrdemServico;
import model.entities.Usuario;


public class CacheHeap implements Serializable {
    private class Node {
        int prioridade;
        OrdemServico ordem;

        public Node(int prioridade, OrdemServico ordem) {
            this.prioridade = prioridade;
            this.ordem = ordem;
        }
    }


    private Node[] heap;
    private int tamanho;
    private int ocupacao;
    private float prioridadeMedia;


    public CacheHeap(int tamanho) {
        if (tamanho < 1) {
            throw new IllegalArgumentException("Tamanho inválido");
        }
        this.tamanho = tamanho;
        heap = new Node[this.tamanho];
        ocupacao = 0;
        prioridadeMedia = 0;
    }


    private int pai(int i) {
        return (i - 1) / 2;
    }


    private int esquerdo(int i) {
        return (2 * i) + 1;
    }


    private int direito(int i) {
        return (2 * i) + 2;
    }


    private void trocar(int i, int j) {
        Node temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }


    public void inserir(OrdemServico valor) {
        inserir(0, valor);
    }


    public void inserir(int prioridade, OrdemServico valor) {
        if (ocupacao == tamanho) {
            removerAbaixoDaMedia();
        }

        Node novo = new Node(prioridade, valor);
        heap[ocupacao] = novo;

        int i = ocupacao;
        prioridadeMedia = (prioridadeMedia * ocupacao + prioridade) / (++ocupacao); // Atualizacao da media e ocupacao

        while (i > 0 && heap[pai(i)].prioridade < heap[i].prioridade) {
            trocar(i, pai(i));
            i = pai(i);
        }
    }


    private void removerAbaixoDaMedia() {
        // Remove todos os elementos com prioridade abaixo da média
        for (int i = 0; i < ocupacao; i++) {
            if (heap[i].prioridade < prioridadeMedia) {
                remover(heap[i].ordem.getCodigo());
                i--;
            }
        }

        // Se ainda estiver cheio, remove o de menor prioridade
        if (ocupacao == tamanho) {
            remover(heap[ocupacao - 1].ordem.getCodigo());
        }

        // Atualiza a média
        prioridadeMedia = 0;
        for (int i = 0; i < ocupacao; i++) {
            prioridadeMedia += heap[i].prioridade;
        }

        prioridadeMedia /= ocupacao;
    }


    public void atualizar(OrdemServico newOrdem) {
        for (int i = 0; i < ocupacao; i++) {
            if (heap[i].ordem.getCodigo() == newOrdem.getCodigo()) {
                heap[i].ordem = newOrdem;
                return;
            }
        }
        
        throw new IllegalArgumentException("Ordem de Serviço não encontrada");
    }


    public OrdemServico buscar(int codigo) {
        for (int i = 0; i < ocupacao; i++) {
            if (heap[i].ordem.getCodigo() == codigo) {
                heap[i].prioridade++;

                if (heap[i].prioridade >= tamanho) {
                    normalizarPrioridades();
                }

                prioridadeMedia = (prioridadeMedia * ocupacao + heap[i].prioridade) / ocupacao;
                return heap[i].ordem;
            }
        }
        
        return null;
    }


    private void normalizarPrioridades() {
        for (int i = 0; i < ocupacao; i++) {
            heap[i].prioridade -= prioridadeMedia;
            if (heap[i].prioridade < 0) {
                heap[i].prioridade = 0;
            }
        }
    }


    public void remover(int codigo) {
        for (int i = 0; i < ocupacao; i++) {
            if (heap[i].ordem.getCodigo() == codigo) {
                heap[i] = heap[--ocupacao];
                heapify(i);
                return;
            }
        }
        
        throw new IllegalArgumentException("Ordem de Serviço não encontrada");
    }


    public void removerPrioridade(int prioridade) {
        for (int i = 0; i < ocupacao; i++) {
            if (heap[i].prioridade == prioridade) {
                heap[i] = heap[--ocupacao];
                heapify(i);
                return;
            }
        }
        
        throw new IllegalArgumentException("Prioridade não encontrada");
    }


    private void heapify(int i) {
        int esq = esquerdo(i);
        int dir = direito(i);
        int maior = i;

        if (esq < ocupacao && heap[esq].prioridade > heap[maior].prioridade) {
            maior = esq;
        }

        if (dir < ocupacao && heap[dir].prioridade > heap[maior].prioridade) {
            maior = dir;
        }

        if (maior != i) {
            trocar(i, maior);
            heapify(maior);
        }
    }


    public int getTamanho() {
        return tamanho;
    }


    public int getOcupacao() {
        return ocupacao;
    }


    public float getPrioridadeMedia() {
        return prioridadeMedia;
    }


    public String toString() {
        // ["(codigo1: prioridade)", "(codigo2: prioridade)"]
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < ocupacao; i++) {
            sb.append("(");
            sb.append(heap[i].ordem.getCodigo());
            sb.append(": ");
            sb.append(heap[i].prioridade);
            sb.append(")");
            if (i < ocupacao - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }


    public void limpar() {
        heap = new Node[tamanho];
        ocupacao = 0;
        prioridadeMedia = 0;
    }


    public static void main(String[] args) {
        // Testar a classe
        CacheHeap cache = new CacheHeap(5);

        Usuario usuario = new Usuario("usuario", "123");
        OrdemServico ordem1 = new OrdemServico("a", "descricao1", usuario);
        OrdemServico ordem2 = new OrdemServico("b", "descricao2", usuario);
        OrdemServico ordem3 = new OrdemServico("c", "descricao3", usuario);
        OrdemServico ordem4 = new OrdemServico("d", "descricao4", usuario);
        OrdemServico ordem5 = new OrdemServico("e", "descricao5", usuario);
        OrdemServico ordem6 = new OrdemServico("f", "descricao6", usuario);


        cache.inserir(0, ordem1);
        cache.inserir(0, ordem2);
        cache.inserir(0, ordem3);
        cache.inserir(0, ordem4);
        cache.inserir(0, ordem5);

        cache.buscar(1);
        cache.buscar(2);
        cache.buscar(1);

        System.out.println(cache);

        cache.inserir(0, ordem6);
        System.out.println(cache);
    }
}