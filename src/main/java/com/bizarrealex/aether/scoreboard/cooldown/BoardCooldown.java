package com.bizarrealex.aether.scoreboard.cooldown;

import java.text.*;
import com.bizarrealex.aether.scoreboard.*;
import org.apache.commons.lang.time.*;

public class BoardCooldown
{
    private static final DecimalFormat SECONDS_FORMATTER;
    private final Board board;
    private final String id;
    private final double duration;
    private final long end;
    
    public BoardCooldown(final Board board, final String id, final double duration) {
        this.board = board;
        this.id = id;
        this.duration = duration;
        this.end = (long)(System.currentTimeMillis() + duration * 1000.0);
        board.getCooldowns().add(this);
    }
    
    public String getFormattedString(final BoardFormat format) {
        if (format == null) {
            throw new NullPointerException();
        }
        if (format == BoardFormat.SECONDS) {
            return BoardCooldown.SECONDS_FORMATTER.format((this.end - System.currentTimeMillis()) / 1000.0f);
        }
        return DurationFormatUtils.formatDuration(this.end - System.currentTimeMillis(), "mm:ss");
    }
    
    public void cancel() {
        this.board.getCooldowns().remove(this);
    }
    
    public Board getBoard() {
        return this.board;
    }
    
    public String getId() {
        return this.id;
    }
    
    public double getDuration() {
        return this.duration;
    }
    
    public long getEnd() {
        return this.end;
    }
    
    static {
        SECONDS_FORMATTER = new DecimalFormat("#0.0");
    }
}
