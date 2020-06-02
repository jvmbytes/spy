package com.jvmbytes.spy.enhance.weaver.asm;

import org.objectweb.asm.Label;

/**
 * TryCatch块,用于ExceptionsTable重排序
 *
 * @author luanjia
 */
public class AsmTryCatchBlock {

    protected final Label start;
    protected final Label end;
    protected final Label handler;
    protected final String type;

    AsmTryCatchBlock(Label start, Label end, Label handler, String type) {
        this.start = start;
        this.end = end;
        this.handler = handler;
        this.type = type;
    }

}