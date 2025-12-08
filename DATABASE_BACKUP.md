# Database Backup & Restore

## Quick Reference

### Backup Database
```bash
./backup-db.sh
```
Creates a timestamped backup in `backups/` directory.

### Restore Database

**Restore most recent backup:**
```bash
./restore-db.sh
```

**Restore specific backup:**
```bash
./restore-db.sh backups/media_library_2025-12-07_19-07-29.sql
```

Both options will ask for confirmation before overwriting your data.

### List Backups
```bash
ls -lh backups/
```

## What You Get

- **Automatic timestamps**: Backups named like `media_library_2025-12-07_19-07-29.sql`
- **Human-readable**: SQL files you can open in a text editor
- **Portable**: Works on any MySQL database, any computer
- **Safe**: Restore script asks for confirmation before overwriting

## Recommended Backup Strategy

1. **Before major changes**: Run `./backup-db.sh`
2. **Regular backups**: Set up a cron job or run weekly
3. **Store safely**: Copy `backups/` folder to Dropbox, external drive, etc.

## Recovery

If something goes wrong:
1. Run: `./restore-db.sh` (automatically uses most recent backup)
2. Type `yes` to confirm
3. Done!

Or to restore a specific backup:
1. Find your backups: `ls -lh backups/`
2. Restore it: `./restore-db.sh backups/media_library_YYYY-MM-DD_HH-MM-SS.sql`
3. Type `yes` to confirm
4. Done!

## Notes

- Backups are excluded from git (see `.gitignore`)
- Database stays in Docker volume for best performance
- Scripts require Docker containers to be running
