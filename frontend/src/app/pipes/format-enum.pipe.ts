import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'formatEnum',
})
export class FormatEnumPipe implements PipeTransform {
  transform(value: string | null | undefined): string {
    if (!value) return '';

    const formatted = value.replace(/_/g, ' ').toLowerCase();

    return formatted
      .split(' ')
      .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  }
}
