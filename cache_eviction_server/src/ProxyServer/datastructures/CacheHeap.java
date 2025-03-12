package ProxyServer.datastructures;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ProxyServer.model.entities.OrdemServico;


public class CacheHeap implements Serializable {
    private class Node implements Serializable {
        int prioridade;
        OrdemServico ordem;

        public Node(int prioridade, OrdemServico ordem) {
            this.prioridade = prioridade;
            this.ordem = ordem;
        }
    }

    private static final long serialVersionUID = 1L;

    private Node[] heap;
    private int tamanho;
    private int ocupacao;
    private float prioridadeMedia;
    private final Lock lock = new ReentrantLock(); // Lock for thread safety

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
        lock.lock();
        try {
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
        } finally {
            lock.unlock();
        }
    }

    public void update(OrdemServico ordem) {
        lock.lock();
        try {
            for (int i = 0; i < ocupacao; i++) {
                if (heap[i].ordem.getCodigo() == ordem.getCodigo()) {
                    heap[i].ordem = ordem;
                    return;
                }
            }
        } finally {
            lock.unlock();
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
        lock.lock();
        try {
            for (int i = 0; i < ocupacao; i++) {
                if (heap[i].ordem.getCodigo() == newOrdem.getCodigo()) {
                    heap[i].ordem = newOrdem;
                    return;
                }
            }

            throw new IllegalArgumentException("Ordem de Serviço não encontrada");
        } finally {
            lock.unlock();
        }
    }

    public OrdemServico buscar(int codigo) {
        lock.lock();
        try {
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
        } finally {
            lock.unlock();
        }
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
        lock.lock();
        try {
            for (int i = 0; i < ocupacao; i++) {
                if (heap[i].ordem.getCodigo() == codigo) {
                    heap[i] = heap[--ocupacao];
                    heapify(i);
                    return;
                }
            }

            throw new IllegalArgumentException("Ordem de Serviço não encontrada");
        } finally {
            lock.unlock();
        }
    }

    public void removerPrioridade(int prioridade) {
        lock.lock();
        try {
            for (int i = 0; i < ocupacao; i++) {
                if (heap[i].prioridade == prioridade) {
                    heap[i] = heap[--ocupacao];
                    heapify(i);
                    return;
                }
            }

            throw new IllegalArgumentException("Prioridade não encontrada");
        } finally {
            lock.unlock();
        }
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
        lock.lock();
        try {
            return tamanho;
        } finally {
            lock.unlock();
        }
    }

    public int getOcupacao() {
        lock.lock();
        try {
            return ocupacao;
        } finally {
            lock.unlock();
        }
    }

    public float getPrioridadeMedia() {
        lock.lock();
        try {
            return prioridadeMedia;
        } finally {
            lock.unlock();
        }
    }

    public OrdemServico[] getOrdens() {
        lock.lock();
        try {
            OrdemServico[] ordens = new OrdemServico[ocupacao];
            for (int i = 0; i < ocupacao; i++) {
                ordens[i] = heap[i].ordem;
            }
            return ordens;
        } finally {
            lock.unlock();
        }
    }

    public OrdemServico[] getOrdensByUsuario(String cpf) {
        lock.lock();
        try {
            OrdemServico[] ordens = new OrdemServico[ocupacao];
            int j = 0;
            for (int i = 0; i < ocupacao; i++) {
                if (heap[i].ordem.getUsuario().getCpf().equals(cpf)) {
                    ordens[j] = heap[i].ordem;
                    j++;
                }
            }
            return ordens;
        } finally {
            lock.unlock();
        }
    }

    public String toString() {
        lock.lock();
        try {
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
        } finally {
            lock.unlock();
        }
    }

    public void limpar() {
        lock.lock();
        try {
            heap = new Node[tamanho];
            ocupacao = 0;
            prioridadeMedia = 0;
        } finally {
            lock.unlock();
        }
    }
}