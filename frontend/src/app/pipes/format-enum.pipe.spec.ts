import { FormatEnumPipe } from './format-enum.pipe';

describe('FormatEnumPipe', () => {
  let pipe: FormatEnumPipe;

  beforeEach(() => {
    pipe = new FormatEnumPipe();
  });

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should format enum with underscores to title case', () => {
    expect(pipe.transform('GAMEBOY_ADVANCE')).toBe('Gameboy Advance');
  });

  it('should format IN_PROGRESS to In Progress', () => {
    expect(pipe.transform('IN_PROGRESS')).toBe('In Progress');
  });

  it('should format single word enum to title case', () => {
    expect(pipe.transform('BOOK')).toBe('Book');
    expect(pipe.transform('MOVIE')).toBe('Movie');
  });

  it('should handle multiple underscores', () => {
    expect(pipe.transform('SUPER_NINTENDO_ENTERTAINMENT_SYSTEM')).toBe(
      'Super Nintendo Entertainment System',
    );
  });

  it('should return empty string for null', () => {
    expect(pipe.transform(null)).toBe('');
  });

  it('should return empty string for undefined', () => {
    expect(pipe.transform(undefined)).toBe('');
  });

  it('should return empty string for empty string', () => {
    expect(pipe.transform('')).toBe('');
  });

  it('should handle lowercase input', () => {
    expect(pipe.transform('already_lowercase')).toBe('Already Lowercase');
  });

  it('should handle mixed case input', () => {
    expect(pipe.transform('MiXeD_CaSe')).toBe('Mixed Case');
  });
});
