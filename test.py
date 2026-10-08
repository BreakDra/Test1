from datetime import datetime
import json
import sys
import psutil
import time


def snapshot(proc: psutil.Process) -> dict:
  """Chụp trạng thái hiện tại của một tiến trình."""
  try:
    return {
        "pid": proc.pid,
        "name": proc.name(),
        "cmd": " ".join(proc.cmdline()),
        "files": [f.path for f in proc.open_files()],
        "conns": [
            {
                "laddr": str(c.laddr),
                "raddr": str(c.raddr) if c.raddr else None,
                "status": c.status,
            }
            for c in proc.net_connections()
        ],
    }
  except (psutil.NoSuchProcess, psutil.AccessDenied):
    return {}


def watch(root_pid: int, duration_s: int = 60):
  try:
    root = psutil.Process(root_pid)
  except psutil.NoSuchProcess:
    print(f"PID {root_pid} không tồn tại")
    return

  seen_pids, events = set(), []
  deadline = time.time() + duration_s

  while time.time() < deadline:
    try:
      family = [root] + root.children(recursive=True)
    except psutil.NoSuchProcess:
      break

    for p in family:
      if p.pid in seen_pids:
        continue

      seen_pids.add(p.pid)
      snap = snapshot(p)

      if snap:
        snap["ts"] = datetime.utcnow().isoformat()
        events.append(snap)
        print(f"[+] PID mới: {snap['pid']} - {snap['cmd']}")

      time.sleep(0.5)

  with open("timeline.json", "w") as f:
    json.dump(events, f, indent=2, ensure_ascii=False)

  print(f"\nĐã ghi {len(events)} sự kiện vào timeline.json")


if __name__ == "__main__":
  watch(int(sys.argv[1]), duration_s=120)
