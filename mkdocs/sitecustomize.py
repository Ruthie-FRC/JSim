import importlib


def _patch_mkdoxy_index_buckets() -> None:
    try:
        generator_base = importlib.import_module("mkdoxy.generatorBase")
    except Exception:
        return

    if "[" not in generator_base.LETTERS:
        generator_base.LETTERS += "["


_patch_mkdoxy_index_buckets()