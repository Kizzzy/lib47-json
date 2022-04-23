package cn.kizzzy.har;

import cn.kizzzy.vfs.IPackage;
import cn.kizzzy.vfs.handler.JsonFileHandler;
import cn.kizzzy.vfs.pack.FilePackage;
import cn.kizzzy.vfs.tree.FileTreeBuilder;
import cn.kizzzy.vfs.tree.Leaf;
import cn.kizzzy.vfs.tree.Node;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;

public class TestOfLoadHar {
    
    public static void exportSwf(String game, String host) {
        String loadRoot = String.format("E:\\88Extrator\\4399\\%s\\Har", game);
        String saveRoot = String.format("E:\\88Extrator\\4399\\%s\\Swf", game);
        
        IPackage loadVfs = new FilePackage(loadRoot, new FileTreeBuilder(loadRoot).build());
        loadVfs.getHandlerKvs().put(HarFile.class, new JsonFileHandler<>(HarFile.class));
        
        IPackage saveVfs = new FilePackage(saveRoot);
        
        List<Node> nodes = loadVfs.listNode("");
        for (Node node : nodes) {
            listNodeImpl(node, loadVfs, saveVfs, host);
        }
    }
    
    private static void listNodeImpl(Node node, IPackage loadVfs, IPackage saveVfs, String host) {
        if (node.leaf) {
            Leaf leaf = (Leaf) node;
            
            HarFile harFile = loadVfs.load(leaf.path, HarFile.class);
            if (harFile != null) {
                for (Entry entry : harFile.log.entries) {
                    if (entry.request.url.contains(".swf") &&
                        entry.request.url.contains(host) &&
                        entry.response.content != null && entry.response.content.size > 0) {
                        String path = getPath(host, entry.request.url);
                        
                        try {
                            System.out.println("export: " + path);
                            
                            byte[] data = Base64.getDecoder().decode(entry.response.content.text);
                            saveVfs.save(path, data);
                        } catch (Exception e) {
                            System.out.println("  export failed: " + entry.request.url);
                        }
                    }
                }
            }
        } else {
            for (Node child : node.children.values()) {
                listNodeImpl(child, loadVfs, saveVfs, host);
            }
        }
    }
    
    private static String getPath(String host, String url) {
        url = url.substring(host.length() + 1);
        int index = url.lastIndexOf('?');
        if (index == -1) {
            return url;
        }
        return url.substring(0, index);
    }
    
    private static void decodeSwf(String game) {
        String root = String.format("E:\\88Extrator\\4399\\%s\\Swf", game);
        
        IPackage vfs = new FilePackage(root, new FileTreeBuilder(root).build());
        
        List<Node> nodes = vfs.listNode("");
        for (Node node : nodes) {
            listNodeImpl(node, vfs);
        }
    }
    
    private static void listNodeImpl(Node node, IPackage vfs) {
        if (node.leaf) {
            Leaf leaf = (Leaf) node;
            if (leaf.path.endsWith(".swf")) {
                byte[] data = vfs.load(node.name, byte[].class);
                if (data != null) {
                    if (data[1] != 0x57 && data[2] != 0x53) {
                        System.out.println("decrypt: " + node.name);
                        
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bos.write(data, 300, 25);
                        bos.write(data, 0, 300);
                        bos.write(data, 325, data.length - 325);
                        
                        vfs.save(leaf.path.replace(".swf", "_decrypt.swf"), bos.toByteArray());
                    }
                }
            }
        } else {
            for (Node child : node.children.values()) {
                listNodeImpl(child, vfs);
            }
        }
    }
    
    public static void main(String[] args) {
        for (String arg : args) {
            System.out.println(arg);
        }
        //exportSwf("赛尔号", "https://seer.61.com");
        //exportSwf("洛克王国", "https://res.17roco.qq.com");
        //exportSwf("卡布西游", "http://enter.wanwan4399.com/bin-debug");
        //exportSwf("功夫派", "http://gf.61.com");
        //exportSwf("龙斗士", "http://lds.100bt.com");
        //exportSwf("三国小镇", "http://sbai.4399.com/4399swf/upload_swf/ftp7/fanyiss/20120106/2");
        //exportSwf("造梦西游2", "http://sbai.4399.com/4399swf/upload_swf/ftp6/hanbao/20110927/4");
        //exportSwf("造梦西游3", "http://sbai.4399.com/4399swf/upload_swf/ftp7/hanbao/20120107/6");
        //exportSwf("造梦西游4", "http://sbai.4399.com/4399swf/upload_swf/ftp15/csya/20150127/1");
        
        exportSwf("QQ农场", "https://appimg1.qq.com/happyfarm");
        
        //decodeSwf("造梦西游2");
        //decodeSwf("造梦西游3");
        //decodeSwf("造梦西游4");
    }
}
