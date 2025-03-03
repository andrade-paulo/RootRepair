package datastructures.Huffman;

public class NodeHuffman {
    int freq;
    char caractere;
    NodeHuffman esquerda;
    NodeHuffman direita;

    public NodeHuffman() {}

    public NodeHuffman (int freq, char caractere, NodeHuffman esquerda, NodeHuffman direita) {
        this.freq = freq;
        this.caractere = caractere;
        this.esquerda = esquerda;
        this.direita = direita;
    }

    public NodeHuffman(int freq, char caractere) {
        this.freq = freq;
        this.caractere = caractere;
        this.esquerda = null;
        this.direita = null;
    }
}
